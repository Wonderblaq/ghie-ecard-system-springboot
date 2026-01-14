package com.registrations.GhIE_ecard.controllers;

import java.lang.Iterable;
import java.util.List;
import java.util.Optional;

import com.registrations.GhIE_ecard.models.Member;
import com.registrations.GhIE_ecard.repositories.AdminRepository;
import com.registrations.GhIE_ecard.repositories.MemberRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PatchMapping;
import com.registrations.GhIE_ecard.services.MemberUpdateDTO;

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


    public AdminController(MemberRepository memberRepository, AdminRepository adminRepository) {
        this.memberRepository = memberRepository;
        this.adminRepository = adminRepository;
    }

    // This is where methods for handling specific HTTP requests (GET, POST, etc.) would be added.

    // Get request for admin to view all registered members
    @GetMapping("/members")
    public Iterable<Member> getAllMembers() {
        return memberRepository.findAll(Sort.by(Sort.Direction.ASC, "memberId"));
    }

    // Get request for admin to find specific members
    @GetMapping("/members/{id}")
    public Member findMember(@PathVariable("id") Long id) {
        Optional<Member> memberToFind = memberRepository.findById(id);
        if (memberToFind.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "member not found!");
        }
        return memberToFind.get();

    }

    // Delete a member from the database
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Member> deleteMember(@PathVariable("id") Long id) {
        Optional<Member> memberToDelete = memberRepository.findById(id);
        if (memberToDelete.isPresent()) {
            Member foundMember = memberToDelete.get();
            memberRepository.deleteById(id);
            return ResponseEntity.ok(foundMember);
        }
        return ResponseEntity.notFound().build();
    }

    // Patch request for member data updates (email and contact)
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
        Optional<Member> updatedMember = adminRepository.findById(id);


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

    // View members yet to receive cards
    @GetMapping("/members/pending-cards")
    public ResponseEntity<List<Member>> viewPendingCards(){
        List<Member> pendingMembers = adminRepository.findByEmailSentFalseOrderByMemberIdAsc();
        if (!pendingMembers.isEmpty()){
            return ResponseEntity.ok(pendingMembers);

        }
        return ResponseEntity.noContent().build();

    }




}
