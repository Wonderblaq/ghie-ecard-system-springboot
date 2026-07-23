package com.registrations.GhIE_ecard.controllers;

import com.registrations.GhIE_ecard.DTO.ProEngineerRegisterDTO;
import com.registrations.GhIE_ecard.enums.Institution;
import com.registrations.GhIE_ecard.enums.Regions;
import com.registrations.GhIE_ecard.models.ProfessionalEngineer;
import com.registrations.GhIE_ecard.repositories.ProfEngineerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@RestController
@RequestMapping("/professionals")
public class ProfessionalRegistrationController {

    private static final Logger log = LoggerFactory.getLogger(ProfessionalRegistrationController.class);
    private final ProfEngineerRepository professionalRepository;
    private final String UPLOAD_DIR = "uploads/supporting_docs/";

    public ProfessionalRegistrationController(ProfEngineerRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerProfessional(
            @RequestPart("data") ProEngineerRegisterDTO request,
            @RequestPart(value = "file", required = true) MultipartFile file) {

        // File Validation
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Please upload a valid supporting document.");
        }

        log.info("Received professional engineer registration request for email: {}", request.getEmail());

        //  Data Validation & Pre-checks
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Error: Email field is required.");
        }
        if (request.getMembershipNumber() == null || request.getMembershipNumber().isBlank()) {
            return ResponseEntity.badRequest().body("Error: Membership number is required.");
        }

        String cleanEmail = request.getEmail().toLowerCase().trim();

        if (professionalRepository.findByEmail(cleanEmail).isPresent()) {
            return ResponseEntity.badRequest().body("Error: A professional engineer with this email already exists.");
        }
        if (professionalRepository.findByMembershipNumber(request.getMembershipNumber().trim()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: This GhIE Membership number is already registered.");
        }

        // File Save Execution
        Path filePath;
        try {
            File targetDir = new File(UPLOAD_DIR);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            String originalFile = file.getOriginalFilename();
            String uniqueFile = request.getMembershipNumber() + "_" + originalFile;
            filePath = Paths.get(UPLOAD_DIR, uniqueFile);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to save file to disk: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error: Failed to store file on server.");
        }

        // Entity Mapping
        ProfessionalEngineer engineer = new ProfessionalEngineer();

        engineer.setFullName(request.getFullName().toUpperCase());
        engineer.setGender(request.getGender().toUpperCase());
        engineer.setPhone(request.getPhone());
        engineer.setEmail(cleanEmail);
        engineer.setLinkedin(request.getLinkedin());

        if (request.getRegion() != null) {
            engineer.setRegion(Regions.valueOf(request.getRegion()));
        }
        engineer.setCityTown(request.getCityTown());

        // Date Handling
        try {
            if (request.getDob() != null && !request.getDob().isBlank()) {
                engineer.setDateOfBirth(LocalDate.parse(request.getDob().trim()));
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

        // Experience Transformation
        try {
            if (request.getYearsExperience() != null && !request.getYearsExperience().isBlank()) {
                engineer.setYearsExperience(Integer.parseInt(request.getYearsExperience().trim()));
            } else {
                engineer.setYearsExperience(0);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Error: Years of experience must be a valid number.");
        }

        // Domains
        engineer.setEngineeringDiscipline(request.getEngineeringDiscipline());
        engineer.setSpecialization(request.getSpecialization());
        engineer.setIndustryWork(request.getIndustryWork());

        // Credentials & File path mapping
        engineer.setQualification(request.getQualification());
        if (request.getInstitutions() != null) {
            engineer.setInstitutions(Institution.valueOf(request.getInstitutions()));
        }
        engineer.setProgramOfStudy(request.getProgramOfStudy());
        engineer.setCertifications(request.getCertifications());

        // Link the saved file path
        engineer.setSupportingFilePath(filePath.toString());

        if (request.getComments() != null) {
            engineer.setComments(request.getComments().toLowerCase());
        }

        // Database Save
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