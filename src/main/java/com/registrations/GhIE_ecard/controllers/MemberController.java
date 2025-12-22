package com.registrations.GhIE_ecard.controllers;

// Import necessary Spring annotations for REST controllers
import com.registrations.GhIE_ecard.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Import repositories and services needed by this controller
import com.registrations.GhIE_ecard.services.GenerateID;
import com.registrations.GhIE_ecard.repositories.MemberRepository;

import java.util.List;


/**
 * MemberController handles HTTP requests related to member registration.
 * It is marked as a REST controller to process web requests.
 */
@RestController
// Maps all requests starting with /member_registration to this controller
@RequestMapping("/registration")
public class MemberController {

    // Autowired fields for dependency injection
    /**
     * Service used for generating unique IDs.
     * @Autowired annotation injects the implementation of GenerateID.
     */
    @Autowired
    private GenerateID generateID;

    // Repository interface for accessing Admin data in the database.
    private final MemberRepository memberRepository;


    //Constructor for MemberController.
    public MemberController(MemberRepository memberRepository, GenerateID generateID){
        this.memberRepository = memberRepository;
        this.generateID = generateID;

    }
    // This is where methods for handling specific HTTP requests (GET, POST, etc.) would be added.

    // Get all members
    @GetMapping("/all")
    public List<Member> getAllMembers(){
        return (List<Member>) memberRepository.findAll();
    }


    // Post request to register a new member with a unique ID
    @PostMapping("/register")
    public String member(@RequestBody Member student){
        // generate ID for each student that gets saved into the database
        student.setMemberId(generateID.generateMemberId());

        Member newMember;
        newMember = memberRepository.save(student);

        return newMember.getFullName() + " " + "saved successfully";

    }



}
