package com.registrations.GhIE_ecard.services;

import com.registrations.GhIE_ecard.enums.Regions;
import com.registrations.GhIE_ecard.models.Admin;
import com.registrations.GhIE_ecard.models.StudentMember;
import com.registrations.GhIE_ecard.repositories.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
This is a service class used for decision-making & multi-tenant access control:
Checks whether Admin accessing system is Super, GhIE, Secretary, or Regional Admin.
*/
@Service
public class MemberService {

    public final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<StudentMember> getMembersForLoggedInAdmin(Admin loggedAdmin) {
        String role = loggedAdmin.getRole();

        // 1. SUPER_ADMIN, GhIE_ADMIN, and SECRETARY skip regional checks entirely and get ALL members
        if ("SUPER_ADMIN".equalsIgnoreCase(role) || "GhIE_ADMIN".equalsIgnoreCase(role) || "SECRETARY".equalsIgnoreCase(role)) {
            return memberRepository.findAllByOrderByRegistrationDateDesc();
        }

        // 2. REGIONAL_ADMIN must have valid assigned regions
        if ("REGIONAL_ADMIN".equalsIgnoreCase(role)) {
            Set<Regions> assignedRegions = loggedAdmin.getRegions();
            if (assignedRegions == null || assignedRegions.isEmpty()) {
                return List.of(); // Return empty list if no region assigned
            }
            return memberRepository.findByRegionIn(assignedRegions);
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

        // 3. Multi-Tenant Check: Regional Admin strictly verified against assigned regions
        if ("REGIONAL_ADMIN".equalsIgnoreCase(role)) {
            Set<Regions> regionsSet = loggedAdmin.getRegions();
            if (regionsSet != null && studentMember.getRegion() != null && regionsSet.contains(studentMember.getRegion())) {
                return studentMember;
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: This member belongs to another Region.");
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

        // 3. If Regional Admin, safely check if the member's region is contained in admin's Set<Regions>
        if ("REGIONAL_ADMIN".equalsIgnoreCase(role)) {
            Set<Regions> assignedRegions = loggedAdmin.getRegions();
            if (assignedRegions == null || assignedRegions.isEmpty()) {
                return List.of();
            }

            return searchResults.stream()
                    .filter(member -> member.getRegion() != null && assignedRegions.contains(member.getRegion()))
                    .collect(Collectors.toList());
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role access.");
    }

    public List<StudentMember> getMembersByEmailSentDate(LocalDate date, Admin loggedAdmin) {
        String role = loggedAdmin.getRole();

        // Convert LocalDate to LocalDateTime at 00:00:00 for accurate database comparison
        LocalDateTime startDate = date.atStartOfDay();

        // 1. Super Admins, GhIE Admins, and Secretary can fetch for any region nationwide
        if ("SUPER_ADMIN".equalsIgnoreCase(role) || "GhIE_ADMIN".equalsIgnoreCase(role) || "SECRETARY".equalsIgnoreCase(role)) {
            return memberRepository.findByEmailSentAtGreaterThanEqualOrderByEmailSentAtDesc(LocalDate.from(startDate));
        }

        // 2. Regional Admins can only view records within their assigned regions
        if ("REGIONAL_ADMIN".equalsIgnoreCase(role)) {
            Set<Regions> assignedRegions = loggedAdmin.getRegions();

            if (assignedRegions == null || assignedRegions.isEmpty()) {
                return List.of();
            }

            return memberRepository.findByEmailSentAtGreaterThanEqualAndRegionInOrderByEmailSentAtDesc(LocalDate.from(startDate), assignedRegions);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role access.");
    }

    public List<StudentMember> getMembersByRegistrationDate(LocalDate date, Admin loggedAdmin) {
        String role = loggedAdmin.getRole();

        // 1. Super Admins, GhIE Admins, and Secretary can fetch across all regions
        if ("SUPER_ADMIN".equalsIgnoreCase(role) || "GhIE_ADMIN".equalsIgnoreCase(role) || "SECRETARY".equalsIgnoreCase(role)) {
            return memberRepository.findByRegistrationDateGreaterThanEqualOrderByRegistrationDateDesc(date);
        }

        // 2. Regional Admins fetch only within their assigned regions
        if ("REGIONAL_ADMIN".equalsIgnoreCase(role)) {
            Set<Regions> assignedRegions = loggedAdmin.getRegions();

            if (assignedRegions == null || assignedRegions.isEmpty()) {
                return List.of();
            }

            return memberRepository.findByRegistrationDateGreaterThanEqualAndRegionInOrderByRegistrationDateDesc(date, assignedRegions);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role access.");
    }
}