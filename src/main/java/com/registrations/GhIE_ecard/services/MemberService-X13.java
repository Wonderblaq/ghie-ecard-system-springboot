package com.registrations.GhIE_ecard.services;
import com.registrations.GhIE_ecard.enums.Regions;
import com.registrations.GhIE_ecard.models.Admin;
import com.registrations.GhIE_ecard.models.StudentMember;
import com.registrations.GhIE_ecard.repositories.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
This is a service class which would be used for decision-making :
Checks whether Admin accessing system is Super or Campus Admin to assign their specific roles;

 */
@Service
public class MemberService {
    public final MemberRepository memberRepository;

    public MemberService( MemberRepository memberRepository) {

        this.memberRepository = memberRepository;
    }


    public List<StudentMember> getMembersForLoggedInAdmin(Admin loggedAdmin){
        String role = loggedAdmin.getRole();
        // GuardRailLogic, if logged in admin is 'SUPER_ADMIN' or 'GHIE_ADMIN',grant them access to everything
        if ("SUPER_ADMIN".equalsIgnoreCase(role) ||
                "GhIE_ADMIN".equalsIgnoreCase(role)){
            return memberRepository.findAll();
        }
        // MultiTenant Scope, if Campus coordinator, restrict to their institution
        // Tenancy change from campus to regional coordinator
        if ("REGIONAL_ADMIN".equalsIgnoreCase(loggedAdmin.getRole())) {
            Set<Regions> assignedRegions = loggedAdmin.getRegions();
            if(assignedRegions == null || assignedRegions.isEmpty()){
                return Collections.emptyList();
            }
            return memberRepository.findByRegionIn(assignedRegions);
        }
        // Fallback, if admin meets none of the roles specified, return an empty list
        return Collections.emptyList();

    }
    public StudentMember getSingleMemberForLoggedInAdmin(Long id, Admin loggedAdmin) {
        // 1. Fetch the member from the database first
        StudentMember studentMember = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found!"));

        // 2. Guardrail: If Super Admin, let them see any member profile across the country
        String role = loggedAdmin.getRole();
        if ("SUPER_ADMIN".equalsIgnoreCase(role )|| "GhIE_ADMIN".equalsIgnoreCase(role)) {
            return studentMember;
        }

        // 3. Multi-Tenant Check: If Regional Admin, strictly verify institutional alignment
        if ("REGIONAL_ADMIN".equalsIgnoreCase(loggedAdmin.getRole())) {
            // Compare the Enum or String values of both Regions
            Set<Regions> regionsSet = loggedAdmin.getRegions();
            if (regionsSet != null && regionsSet.contains(studentMember.getRegion())) {
                return studentMember;
            } else {
                // Throw a 403 Forbidden if a coordinator tries to access another school's data
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: This member belongs to another Region.");
            }
        }

        // Fallback security clause
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role access.");
    }
    public List<StudentMember> searchMembersForAdmin(String query, Admin loggedAdmin) {
        // 1. Fetch matching members from repo
        List<StudentMember> searchResults = memberRepository.searchMembers(query);

        // 2. If SUPER_ADMIN, return all results
        if ("SUPER_ADMIN".equals(loggedAdmin.getRole())) {
            return searchResults;
        }

        // If Regional Admin, filter results to match their institution
        return searchResults.stream()
                .filter(member -> {
                    return member.getRegion().equals(loggedAdmin.getRegions());
                })
                .collect(Collectors.toList());
    }

// method to view members on the dates their cards were sent
    public List<StudentMember> getMembersByEmailSentDate(LocalDate date, Admin loggedAdmin) {
        String role = loggedAdmin.getRole();

        // 1. Super Admins, GhIE Admins, and Secretary can fetch for any region nationwide
        if ("SUPER_ADMIN".equalsIgnoreCase(role) ||
                "GhIE_ADMIN".equalsIgnoreCase(role) ) {
            return memberRepository.findByEmailSentAtDate(date);
        }

        // 2. Regional Admins can only view records within their assigned regions
        if ("REGIONAL_ADMIN".equalsIgnoreCase(role)) {
            Set<Regions> assignedRegions = loggedAdmin.getRegions();

            if (assignedRegions == null || assignedRegions.isEmpty()) {
                return List.of();
            }

            return memberRepository.findByEmailSentAtDateAndRegionIn(date, assignedRegions);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role access.");
    }

    public List<StudentMember> getMembersByRegistrationDate(LocalDate date, Admin loggedAdmin) {
        String role = loggedAdmin.getRole();

        // Super Admins, GhIE Admins can fetch across all regions
        if ("SUPER_ADMIN".equalsIgnoreCase(role) ||
                "GhIE_ADMIN".equalsIgnoreCase(role)) {
            return memberRepository.findByRegistrationDate(date);
        }

        // Regional Admins fetch only within their assigned regions
        if ("REGIONAL_ADMIN".equalsIgnoreCase(role)) {
            Set<Regions> assignedRegions = loggedAdmin.getRegions();

            if (assignedRegions == null || assignedRegions.isEmpty()) {
                return List.of();
            }

            return memberRepository.findByRegistrationDateAndRegionIn(date, assignedRegions);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role access.");
    }
    //
}
