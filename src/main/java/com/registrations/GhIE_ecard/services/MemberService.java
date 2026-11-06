package com.registrations.GhIE_ecard.services;

import com.registrations.GhIE_ecard.enums.Institution;
import com.registrations.GhIE_ecard.models.Admin;
import com.registrations.GhIE_ecard.models.StudentMember;
import com.registrations.GhIE_ecard.repositories.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
This is a service class used for decision-making & multi-tenant access control:
Checks whether Admin accessing system is Super, GhIE, Secretary, or Regional/Institutional Admin.
*/
@Service
public class MemberService {

    public final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<StudentMember> getMembersForLoggedInAdmin(Admin loggedAdmin) {
        String role = loggedAdmin.getRole();

        // 1. SUPER_ADMIN, GhIE_ADMIN, and SECRETARY skip institutional checks entirely and get ALL members
        if ("SUPER_ADMIN".equalsIgnoreCase(role) || "GhIE_ADMIN".equalsIgnoreCase(role) || "SECRETARY".equalsIgnoreCase(role)) {
            return memberRepository.findAllByOrderByRegistrationDateDesc();
        }

        // 2. REGIONAL_ADMIN must have valid assigned institutions
        if ("REGIONAL_ADMIN".equalsIgnoreCase(role)) {
            Set<Institution> assignedInstitutions = loggedAdmin.getAssignedInstitutions();
            if (assignedInstitutions == null || assignedInstitutions.isEmpty()) {
                return List.of(); // Return empty list if no institution assigned
            }
            return memberRepository.findByInstitutionIn(assignedInstitutions);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role access.");
    }

    public StudentMember getSingleMemberForLoggedInAdmin(Long id, Admin loggedAdmin) {
        // 1. Fetch the member from the database first
        StudentMember studentMember = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found!"));

        String role = loggedAdmin.getRole();

        // 2. Guardrail: Super Admins, GhIE Admins, and Secretary can view any member nationwide
        if ("SUPER_ADMIN".equalsIgnoreCase(role) || "GhIE_ADMIN".equalsIgnoreCase(role) || "SECRETARY".equalsIgnoreCase(role)) {
            return studentMember;
        }

        // 3. Multi-Tenant Check: Regional Admin strictly verified against assigned institutions
        if ("REGIONAL_ADMIN".equalsIgnoreCase(role)) {
            Set<Institution> institutionsSet = loggedAdmin.getAssignedInstitutions();
            if (institutionsSet != null && studentMember.getInstitution() != null && institutionsSet.contains(studentMember.getInstitution())) {
                return studentMember;
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: This member belongs to another Institution.");
            }
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role access.");
    }

    public List<StudentMember> searchMembersForAdmin(String query, Admin loggedAdmin) {
        // 1. Fetch matching members from repo
        List<StudentMember> searchResults = memberRepository.searchMembers(query);

        String role = loggedAdmin.getRole();

        // 2. If SUPER_ADMIN, GhIE_ADMIN, or SECRETARY, return all results
        if ("SUPER_ADMIN".equalsIgnoreCase(role) || "GhIE_ADMIN".equalsIgnoreCase(role) || "SECRETARY".equalsIgnoreCase(role)) {
            return searchResults;
        }

        // 3. If Regional Admin, safely check if the member's institution is contained in admin's Set<Institution>
        if ("REGIONAL_ADMIN".equalsIgnoreCase(role)) {
            Set<Institution> assignedInstitutions = loggedAdmin.getAssignedInstitutions();
            if (assignedInstitutions == null || assignedInstitutions.isEmpty()) {
                return List.of();
            }

            return searchResults.stream()
                    .filter(member -> member.getInstitution() != null && assignedInstitutions.contains(member.getInstitution()))
                    .collect(Collectors.toList());
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role access.");
    }

    public List<StudentMember> getMembersByEmailSentDateRange(LocalDate startDate, LocalDate endDate, Admin loggedAdmin) {
        if (loggedAdmin == null || loggedAdmin.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User authentication required.");
        }

        // 1. Input Validation
        if (startDate == null || endDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both start date and end date must be provided.");
        }

        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date.");
        }

        // 2. Convert LocalDate boundaries to full day timestamps (00:00:00 to 23:59:59.999999999)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        String role = loggedAdmin.getRole();

        // 3. Super Admins, GhIE Admins, and Secretary fetch across all institutions
        if ("SUPER_ADMIN".equalsIgnoreCase(role) || "GhIE_ADMIN".equalsIgnoreCase(role) || "SECRETARY".equalsIgnoreCase(role)) {
            return memberRepository.findByEmailSentAtBetweenOrderByEmailSentAtDesc(startDateTime, endDateTime);
        }

        // 4. Regional Admins fetch only within their assigned institutions
        if ("REGIONAL_ADMIN".equalsIgnoreCase(role)) {
            Set<Institution> assignedInstitutions = loggedAdmin.getAssignedInstitutions();

            if (assignedInstitutions == null || assignedInstitutions.isEmpty()) {
                return List.of();
            }

            return memberRepository.findByEmailSentAtBetweenAndInstitutionInOrderByEmailSentAtDesc(
                    startDateTime,
                    endDateTime,
                    assignedInstitutions
            );
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role access.");
    }



    public List<StudentMember> getMembersByRegistrationDateRange(LocalDate startDate, LocalDate endDate, Admin loggedAdmin) {
        if (loggedAdmin == null || loggedAdmin.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User authentication required.");
        }

        // Input Validation
        if (startDate == null || endDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both start date and end date must be provided.");
        }

        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date.");
        }

        String role = loggedAdmin.getRole();

        // 1. Super Admins, GhIE Admins, and Secretary can fetch across all institutions
        if ("SUPER_ADMIN".equalsIgnoreCase(role) || "GhIE_ADMIN".equalsIgnoreCase(role) || "SECRETARY".equalsIgnoreCase(role)) {
            return memberRepository.findByRegistrationDateBetweenOrderByRegistrationDateDesc(startDate, endDate);
        }

        // 2. Regional Admins fetch only within their assigned institutions
        if ("REGIONAL_ADMIN".equalsIgnoreCase(role)) {
            Set<Institution> assignedInstitutions = loggedAdmin.getAssignedInstitutions();

            if (assignedInstitutions == null || assignedInstitutions.isEmpty()) {
                return List.of();
            }

            return memberRepository.findByRegistrationDateBetweenAndInstitutionInOrderByRegistrationDateDesc(
                    startDate,
                    endDate,
                    assignedInstitutions
            );
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role access.");
    }


}