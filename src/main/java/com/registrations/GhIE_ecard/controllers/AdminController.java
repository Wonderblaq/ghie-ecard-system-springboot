package com.registrations.GhIE_ecard.controllers;

import java.lang.Iterable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Arrays;
import java.util.Optional;

import com.registrations.GhIE_ecard.models.Member;
import com.registrations.GhIE_ecard.repositories.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

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


    public AdminController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // This is where methods for handling specific HTTP requests (GET, POST, etc.) would be added.

    // Get request for admin to view all registered members
    @GetMapping("/members")
    public Iterable<Member> getAllMembers() {
        return memberRepository.findAll();
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
    public Member deleteMember(@PathVariable("id") Long id) {
        Optional<Member> memberToDelete = memberRepository.findById(id);
        if (memberToDelete.isPresent()) {
            Member foundMember = memberToDelete.get();
            memberRepository.deleteById(id);
            return foundMember;
        }
        return null;
    }

    // Put request for member data updates
    @PutMapping("/members/{id}/email")
    public Member updateEmail(@PathVariable("id") Long id) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        if (memberOptional.isPresent()) {
            Member updatedMember = memberOptional.get();
            updatedMember.setEmail(updatedMember.getEmail());
            memberRepository.save(updatedMember);

        }
        return null;

    }
}
