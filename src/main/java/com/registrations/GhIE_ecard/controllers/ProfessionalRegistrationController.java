package com.registrations.GhIE_ecard.controllers;
import com.registrations.GhIE_ecard.enums.Regions;
import com.registrations.GhIE_ecard.models.ProfessionalEngineer;
import com.registrations.GhIE_ecard.DTO.ProEngineerRegisterDTO;
import com.registrations.GhIE_ecard.repositories.ProfEngineerRepository;
import com.registrations.GhIE_ecard.enums.Institution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/professionals")
public class ProfessionalRegistrationController {

    private static final Logger log = LoggerFactory.getLogger(ProfessionalRegistrationController.class);
    private final ProfEngineerRepository professionalRepository;

    // Standard constructor injection
    public ProfessionalRegistrationController(ProfEngineerRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerProfessional(@RequestBody ProEngineerRegisterDTO request) {
        log.info("Received professional engineer registration request for email: {}", request.getEmail());

        // Data Validation & Pre-checks
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Error: Email field is required.");
        }
        if (request.getMembershipNumber() == null || request.getMembershipNumber().isBlank()) {
            return ResponseEntity.badRequest().body("Error: Membership number is required.");
        }

        String cleanEmail = request.getEmail().toLowerCase().trim();

        // Check for Unique Constraint violations BEFORE attempting to save
        if (professionalRepository.findByEmail(cleanEmail).isPresent()) {
            return ResponseEntity.badRequest().body("Error: A professional engineer with this email already exists.");
        }
        if (professionalRepository.findByMembershipNumber(request.getMembershipNumber().trim()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: This GhIE Membership number is already registered.");
        }

        // Entity mapping and type conversions
        ProfessionalEngineer engineer = new ProfessionalEngineer();

        // Personal & Contact Info
        engineer.setFullName(request.getFullName());
        engineer.setGender(request.getGender());
        engineer.setPhone(request.getPhone());
        engineer.setEmail(cleanEmail);
        engineer.setLinkedin(request.getLinkedin());
        engineer.setRegion(Regions.valueOf(request.getRegion()));
        engineer.setCityTown(request.getCityTown());

        // Handle Date conversion safely
        try {
            if (request.getDob() != null && !request.getDob().isBlank()) {
                engineer.setDateOfBirth(LocalDate.parse(request.getDob().trim())); // Expects YYYY-MM-DD
            }
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date of birth '{}': {}", request.getDob(), e.getMessage());
            return ResponseEntity.badRequest().body("Error: Invalid Date of Birth format. Use YYYY-MM-DD.");
        }

        // GhIE Specifics
        engineer.setMembershipNumber(request.getMembershipNumber().trim());
        engineer.setMembershipGrade(request.getMembershipGrade());
        engineer.setBranchDivision(request.getBranchDivision());

        // Professional Context
        engineer.setCurrentEmployer(request.getCurrentEmployer());
        engineer.setJobTitle(request.getJobTitle());
        engineer.setEmploymentStatus(request.getEmploymentStatus());
        engineer.setEmploymentSector(request.getEmploymentSector());

        // Handle numeric experience transformation safely
        try {
            if (request.getYearsExperience() != null && !request.getYearsExperience().isBlank()) {
                engineer.setYearsExperience(Integer.parseInt(request.getYearsExperience().trim()));
            } else {
                engineer.setYearsExperience(0);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Error: Years of experience must be a valid number.");
        }

        // Domains & Experience Metrics
        engineer.setEngineeringDiscipline(request.getEngineeringDiscipline());
        engineer.setSpecialization(request.getSpecialization());
        engineer.setIndustryWork(request.getIndustryWork());

        // Educational credentials
        engineer.setQualification(request.getQualification());
        engineer.setInstitutions(Institution.valueOf(request.getInstitutions()));
        engineer.setProgramOfStudy(request.getProgramOfStudy());
        engineer.setCertifications(request.getCertifications());

        engineer.setComments(request.getComments());

        // Persistence Action
        try {
            professionalRepository.save(engineer);
            log.info("Professional engineer successfully saved with Membership Number: {}", engineer.getMembershipNumber());
            return ResponseEntity.ok("Registration completed successfully!");
        } catch (Exception e) {
            log.error("Database failure while saving professional engineer: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("System Error: Failed to commit registration records.");
        }
    }
}