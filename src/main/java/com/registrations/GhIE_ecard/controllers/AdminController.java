package com.registrations.GhIE_ecard.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.registrations.GhIE_ecard.DTO.RejectMemberRequestDTO;
import com.registrations.GhIE_ecard.models.Admin;
import com.registrations.GhIE_ecard.models.CardProcessingResult;
import com.registrations.GhIE_ecard.models.StudentMember;
import com.registrations.GhIE_ecard.repositories.AdminRepository;
import com.registrations.GhIE_ecard.repositories.MemberRepository;
import com.registrations.GhIE_ecard.repositories.ProfEngineerRepository;
import com.registrations.GhIE_ecard.services.CardDispatchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PatchMapping;
import com.registrations.GhIE_ecard.DTO.MemberUpdateDTO;
import com.registrations.GhIE_ecard.services.MemberService;
import com.registrations.GhIE_ecard.models.ProfessionalEngineer;
import com.registrations.GhIE_ecard.emailServices.*;
/**
 * AdminController handles HTTP requests related to admin responsibilities.
 * It is marked as a REST controller to process web requests.
 */
@RestController
@RequestMapping("/admin")
// Maps all requests starting with /admin to this controller
public class AdminController {

    // Repository interface for accessing Admin data in the database.
    public final EmailService emailService;
    public final MemberRepository memberRepository;
    public final AdminRepository adminRepository;
    private CardDispatchService cardDispatchService;
    public MemberService memberService;
    public ProfEngineerRepository profEngineerRepository;


    public AdminController(EmailService emailService, MemberRepository memberRepository, AdminRepository adminRepository,
                           MemberService memberService,
                           CardDispatchService cardDispatchService,
                           ProfEngineerRepository profEngineerRepository) {
        this.emailService = emailService;
        this.memberRepository = memberRepository;
        this.adminRepository = adminRepository;
        this.cardDispatchService = cardDispatchService;
        this.memberService = memberService;
        this.profEngineerRepository = profEngineerRepository;
    }

    // This is where methods for handling specific HTTP requests (GET, POST, etc.) would be added.

    // Get request for admin to view all registered members
    // Get request for admin to view all registered members (Now handles pagination parameters gracefully)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGIONAL_ADMIN', 'GhIE_ADMIN')")
    @GetMapping("/members")
    public ResponseEntity<?> getAllMembers(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "50") int size,
            @AuthenticationPrincipal Admin loggedAdmin
    ) {
        if (loggedAdmin == null) {
            System.out.println("DEBUG: loggedAdmin is NULL!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin user context not found in JWT");
        }

        System.out.println("DEBUG: Logged admin username: " + loggedAdmin.getUsername());
        List<StudentMember> studentMembers = memberService.getMembersForLoggedInAdmin(loggedAdmin);
        return ResponseEntity.ok(studentMembers);
    }

    // Get request for admins to find specific members
    // 1. VIEW MEMBERS (Secretary CAN access - read-only across all regions)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGIONAL_ADMIN', 'GhIE_ADMIN')")
    @GetMapping("/members/{id}")
    public ResponseEntity<Optional<StudentMember>> findMember(@PathVariable("id") Long id, @AuthenticationPrincipal Admin loggedAdmin) {
        Optional<StudentMember> memberToFind = Optional.ofNullable(memberService.getSingleMemberForLoggedInAdmin(id, loggedAdmin));
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
    public ResponseEntity<StudentMember> deleteMember(@PathVariable("id") Long id) {
        Optional<StudentMember> memberToDelete = memberRepository.findById(id);
        if (memberToDelete.isPresent()) {
            StudentMember foundStudentMember = memberToDelete.get();
            memberRepository.deleteById(id);
            return ResponseEntity.ok(foundStudentMember);
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
        Optional<StudentMember> updatedMember = memberRepository.findById(id);

        if(updatedMember.isPresent()) {
            StudentMember foundStudentMember = updatedMember.get();
            if(updates.newContact != null){
                foundStudentMember.setContact(updates.newContact);
            }
            if(updates.newEmail != null){
                foundStudentMember.setEmail(updates.newEmail);
            }
            memberRepository.save(foundStudentMember);
            return ResponseEntity.ok(foundStudentMember);

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("member not found");
    }

    // View members yet to receive cards, role given to all admins
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGIONAL_ADMIN', 'GhIE_ADMIN')")
    @GetMapping("/members/pending-cards")
    public ResponseEntity<?> viewPendingCards(@AuthenticationPrincipal Admin loggedAdmin){
        List<StudentMember> pendingStudentMembers = memberService.getMembersForLoggedInAdmin(loggedAdmin);
        if (!pendingStudentMembers.isEmpty()){
            return ResponseEntity.ok(pendingStudentMembers);

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

            case "ALREADY SENT":
                // Returning a 400 Bad Request or 200 with a specific message depending on preference.
                // 400 is great because it tells the frontend "You shouldn't have requested this."
                return ResponseEntity.badRequest().body("Card has already been sent to this member previously.");

            case "FAILED":
            default:
                return ResponseEntity.internalServerError().body("Failed to dispatch card to member: " + memberId);



        }


    }
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/professionals")
    public ResponseEntity<?> viewAllProfessionals(@AuthenticationPrincipal Admin loggedAdmin){
        List<ProfessionalEngineer>professionalEngineers = profEngineerRepository.findAll();
        return ResponseEntity.ok(professionalEngineers);

    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/reject-member")
    public ResponseEntity<?> rejectMember(@AuthenticationPrincipal Admin loggedAdmin,
                                          @RequestBody RejectMemberRequestDTO requestDTO) {

        // Safely check if member exists
        Optional<StudentMember> findMember = memberRepository.findByMemberId(requestDTO.getMemberId());
        if (findMember.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Member not found.");
        }

        //Safely check reason string
        String reason = requestDTO.getReason();
        if (reason == null || reason.isBlank()) {
            return ResponseEntity.badRequest().body("Error: Rejection reason cannot be empty.");
        }

        StudentMember rejectedMember = findMember.get();

        // Send email notification
        Boolean emailSent = emailService.sendRegistrationRejection(rejectedMember, reason);

        if (emailSent) {
            // Actually delete the record from the database after email succeeds
            memberRepository.delete(rejectedMember);

            return ResponseEntity.ok("Rejection email successfully sent to " + rejectedMember.getEmail() + " and registration removed.");
        }
        return ResponseEntity.internalServerError().body("Failed to send rejection email. Member record was retained.");
    }

    // Endpoint for querying members
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGIONAL_ADMIN', 'GhIE_ADMIN')")
    @GetMapping("/search-members")
    public ResponseEntity<List<StudentMember>> searchMembers(
            @RequestParam(name = "search", required = false) String search,
            @AuthenticationPrincipal Admin loggedAdmin
    ) {
        List<StudentMember> studentMembers;

        if (search != null && !search.isBlank()) {
            studentMembers = memberService.searchMembersForAdmin(search.trim(), loggedAdmin);
        } else {
            studentMembers = memberService.getMembersForLoggedInAdmin(loggedAdmin);
        }

        return ResponseEntity.ok(studentMembers);
    }
//    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGIONAL_ADMIN', 'GhIE_ADMIN')")
//    @GetMapping("/verify/{memberId}")
//    public ResponseEntity<StudentMember> verifyMemberCard(@PathVariable("memberId") String memberId) {
//        Optional<StudentMember> member = memberRepository.findByMemberId(memberId);
//
//        if (member.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid or unverified GhIE E-Card.");
//        }
//
//        return ResponseEntity.ok(member.get());
//    }
    /**
     * Endpoint for filtering members by the date their email was sent.
     * Example request: GET /admin/members/by-email-date?date=2026-08-03
     */
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGIONAL_ADMIN', 'GhIE_ADMIN')")
    @GetMapping("/members/by-email-date")
    public ResponseEntity<List<StudentMember>> getMembersByEmailSentDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal Admin loggedAdmin) {

        List<StudentMember> members = memberService.getMembersByEmailSentDate(date, loggedAdmin);
        return ResponseEntity.ok(members);
    }

    // Endpoint for filtering members by their registrationDate
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'REGIONAL_ADMIN', 'GhIE_ADMIN')")
    @GetMapping("/members/by-registration-date")
    public ResponseEntity<List<StudentMember>> getMembersByRegistrationDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal Admin loggedAdmin) {

        List<StudentMember> members = memberService.getMembersByRegistrationDate(date, loggedAdmin);
        return ResponseEntity.ok(members);
    }



}
