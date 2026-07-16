package com.registrations.GhIE_ecard.controllers;

import java.lang.classfile.instruction.SwitchCase;
import java.util.List;
import java.util.Optional;

import com.registrations.GhIE_ecard.models.Admin;
import com.registrations.GhIE_ecard.models.CardProcessingResult;
import com.registrations.GhIE_ecard.models.Member;
import com.registrations.GhIE_ecard.repositories.AdminRepository;
import com.registrations.GhIE_ecard.repositories.MemberRepository;
import com.registrations.GhIE_ecard.services.CardDispatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PatchMapping;
import com.registrations.GhIE_ecard.DTO.MemberUpdateDTO;
import com.registrations.GhIE_ecard.services.MemberService;
/**
 * AdminController handles HTTP requests related to admin responsibilities.
 * It is marked as a REST controller to process web requests.
 */
@RestController
@RequestMapping("/admin")
// Maps all requests starting with /admin to this controller
public class AdminController {

    // Repository interface for accessing Admin data in the database.
    public final MemberRepository memberRepository;
    public final AdminRepository adminRepository;
    private CardDispatchService cardDispatchService;
    public MemberService memberService;


    public AdminController(MemberRepository memberRepository, AdminRepository adminRepository,
                           MemberService memberService,
                           CardDispatchService cardDispatchService) {
        this.memberRepository = memberRepository;
        this.adminRepository = adminRepository;
        this.cardDispatchService = cardDispatchService;
        this.memberService = memberService;
    }

    // This is where methods for handling specific HTTP requests (GET, POST, etc.) would be added.

    // Get request for admin to view all registered members

    @GetMapping("/members")
    public ResponseEntity<List<Member>> getAllMembers(
            // this annotation enables spring injects the -
            // fully loaded admin object/profile from jwt context
            @AuthenticationPrincipal Admin loggedAdmin
    ) {
        List<Member> members = memberService.getMembersForLoggedInAdmin(loggedAdmin);

        return ResponseEntity.ok(members);
    }

    // Get request for admins to find specific members
    @GetMapping("/members/{id}")
    public ResponseEntity<Optional<Member>> findMember(@PathVariable("id") Long id, @AuthenticationPrincipal Admin loggedAdmin) {
        Optional<Member> memberToFind = Optional.ofNullable(memberService.getSingleMemberForLoggedInAdmin(id, loggedAdmin));
        if (memberToFind.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "member not found!");
        }
        System.out.println("LOGGED IN USER AUTHORITIES: " + org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        return ResponseEntity.ok(memberToFind);

    }
//
    // Delete a selected member from the database, access granted to only 'SUPER_ADMIN'
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/delete-members/{id}")
    public ResponseEntity<Member> deleteMember(@PathVariable("id") Long id) {
        Optional<Member> memberToDelete = memberRepository.findById(id);
        if (memberToDelete.isPresent()) {
            Member foundMember = memberToDelete.get();
            memberRepository.deleteById(id);
            return ResponseEntity.ok(foundMember);
        }
        return ResponseEntity.notFound().build();
    }

    // Delete all selected members, access granted to only 'SUPER_ADMIN'
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/members/bulk-delete")
    public ResponseEntity<Void> deleteAllMembers(@RequestBody List<Long> ids){
        memberRepository.deleteAllById(ids);
        return ResponseEntity.ok().build();

    }

    // Patch request for member data updates (email and contact), role: SUPER_ADMIN
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PatchMapping("/members/{id}/email")
    public ResponseEntity<?> updateMemberInfo(@RequestBody (required = false) MemberUpdateDTO updates
    , @PathVariable ("id") Long id){
        /* find the member
        * check if member exits or present and retrieve member
        * check if entries for email and contact to be updated are non-null
        * update email and contact
        * Save updates in Database
        *  Show response of success and failure
        * */
        Optional<Member> updatedMember = memberRepository.findById(id);

        if(updatedMember.isPresent()) {
            Member foundMember = updatedMember.get();
            if(updates.newContact != null){
                foundMember.setContact(updates.newContact);
            }
            if(updates.newEmail != null){
                foundMember.setEmail(updates.newEmail);
            }
            memberRepository.save(foundMember);
            return ResponseEntity.ok(foundMember);

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("member not found");
    }

    // View members yet to receive cards, role given to all admins
    @GetMapping("/members/pending-cards")
    public ResponseEntity<?> viewPendingCards(@AuthenticationPrincipal Admin loggedAdmin){
        List<Member> pendingMembers = memberService.getMembersForLoggedInAdmin(loggedAdmin);
        if (!pendingMembers.isEmpty()){
            return ResponseEntity.ok(pendingMembers);

        }
        return ResponseEntity.ok().body("No Pending Members");

    }

    // Allow only 'SUPER_ADMIN', to process and send cards for multiple members
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/process-cards")
    public ResponseEntity<CardProcessingResult> processCards() {
        CardProcessingResult result =
                cardDispatchService.processAllPendingCards();
        return ResponseEntity.ok(result);
    }


    // This Endpoint process sending cards to single members
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/process-single-card/{memberId}")
    public ResponseEntity<String> processSingleCard(@PathVariable String memberId) {
        String result = cardDispatchService.processSingleCard(memberId);
        switch (result) {
            case "SUCCESS":
                return ResponseEntity.ok("Card successfully dispatched to member: " + memberId);

            case "ALREADY_SENT":
                // Returning a 400 Bad Request or 200 with a specific message depending on preference.
                // 400 is great because it tells the frontend "You shouldn't have requested this."
                return ResponseEntity.badRequest().body("Card has already been sent to this member previously.");

            case "FAILED":
            default:
                return ResponseEntity.internalServerError().body("Failed to dispatch card to member: " + memberId);


        }


    }

}
