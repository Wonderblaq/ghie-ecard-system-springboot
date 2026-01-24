package com.registrations.GhIE_ecard.controllers;

// Import necessary Spring annotations for REST controllers
import com.registrations.GhIE_ecard.enums.EnrollmentYear;
import com.registrations.GhIE_ecard.enums.Institution;
import com.registrations.GhIE_ecard.enums.Regions;
import com.registrations.GhIE_ecard.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Import repositories and services needed by this controller
import com.registrations.GhIE_ecard.services.GenerateID;
import com.registrations.GhIE_ecard.repositories.MemberRepository;
import com.registrations.GhIE_ecard.services.MemberRegistrationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


/**
 * MemberController handles HTTP requests related to member registration.
 * It is marked as a REST controller to process web requests.
 */
@RestController
// Maps all requests starting with /registration to this controller
@RequestMapping("/registration")
public class MemberController {

    // Autowired fields for dependency injection
    /**
     * Service used for generating unique IDs.
     * @Autowired annotation injects the implementation of GenerateID.
     */
    @Autowired
    private final GenerateID generateID;
    private final MemberRegistrationService registrationService;

    // Repository interface for accessing member data in the database.
    private final MemberRepository memberRepository;


    //Constructor for MemberController.
    public MemberController(MemberRepository memberRepository, GenerateID generateID, MemberRegistrationService registrationService){
        this.memberRepository = memberRepository;
        this.generateID = generateID;

        this.registrationService = registrationService;
    }
    // This is where methods for handling specific HTTP requests (GET, POST, etc.) would be added.

    // Get all members
    @GetMapping("/all")
    public List<Member> getAllMembers(){
        return (List<Member>) memberRepository.findAll(
                Sort.by(Sort.Direction.ASC, "memberId")
        );
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerMember(@RequestBody Member student) {
        /* * Check for duplicate email,
         * Generate a unique ID for the member
         * Set Expiry and Registration Dates
         * Save Registered Member
        **/
        if (!memberRepository.findByEmail(student.getEmail()).isPresent()){
            student.setMemberId(generateID.generateMemberId());
            student.setRegistrationDate(LocalDate.now());
            student.setExpiryDate(registrationService.calculateExpiryDate(student.getEnrollmentYear()));
            // Save the member
            Member savedMember = memberRepository.save(student);
        }
        else {
            return ResponseEntity.status(HttpStatus.CONFLICT).
                    body("Email has already being registered");
        }
        // Return the saved entity with HTTP 200 OK
        return ResponseEntity.ok("Registered Successfully");
    }

    // Display List of Institution
    @GetMapping("/institutions")
    public List<Institution> getAllInstitutions(){
        return Arrays.asList(Institution.values());
    }

    // Display List of Regions
    @GetMapping("/regions")
    public List<Regions> getAllRegions(){
        return Arrays.asList(Regions.values());
    }

    @GetMapping("/enrollment")
    public List<EnrollmentYear> getAllEnrollmentYears(){
        return Arrays.asList((EnrollmentYear.values()));
    }




    // Get Request for member to view ID card



}
