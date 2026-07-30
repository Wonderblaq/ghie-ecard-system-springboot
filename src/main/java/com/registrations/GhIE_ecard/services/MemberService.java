package com.registrations.GhIE_ecard.services;
import com.registrations.GhIE_ecard.models.Admin;
import com.registrations.GhIE_ecard.models.StudentMember;
import com.registrations.GhIE_ecard.repositories.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
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
        // GuardRailLogic, if logged in admin is 'SUPER_ADMIN',grant them access to everything
        if ("SUPER_ADMIN".equalsIgnoreCase(loggedAdmin.getRole())){
            return memberRepository.findAll();
        }
        // MultiTenant Scope, if Campus coordinator, restrict to their institution
        if ("CAMPUS_ADMIN".equalsIgnoreCase(loggedAdmin.getRole())) {
            return memberRepository.findByInstitution(loggedAdmin.getInstitution());

        }
        // Fallback, if admin meets none of the roles specified, return an empty list
        return Collections.emptyList();

    }
    public StudentMember getSingleMemberForLoggedInAdmin(Long id, Admin loggedAdmin) {
        // 1. Fetch the member from the database first
        StudentMember studentMember = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found!"));

        // 2. Guardrail: If Super Admin, let them see any member profile across the country
        if ("SUPER_ADMIN".equalsIgnoreCase(loggedAdmin.getRole())) {
            return studentMember;
        }

        // 3. Multi-Tenant Check: If Campus Admin, strictly verify institutional alignment
        if ("CAMPUS_ADMIN".equalsIgnoreCase(loggedAdmin.getRole())) {
            // Compare the Enum or String values of both institutions
            if (studentMember.getInstitution() == loggedAdmin.getInstitution()) {
                return studentMember;
            } else {
                // Throw a 403 Forbidden if a coordinator tries to access another school's data
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: This member belongs to another institution.");
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

        // If Campus Admin, filter results to match their institution
        return searchResults.stream()
                .filter(member -> {
                    return member.getRegion().equals(loggedAdmin.getInstitution());
                })
                .collect(Collectors.toList());
    }
    //
}
