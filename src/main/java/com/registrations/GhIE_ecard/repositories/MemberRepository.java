package com.registrations.GhIE_ecard.repositories;

import com.registrations.GhIE_ecard.enums.Institution;
import com.registrations.GhIE_ecard.models.StudentMember;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MemberRepository extends JpaRepository<StudentMember, Long> {

    List<StudentMember> findAllByOrderByRegistrationDateDesc();
    boolean existsByEmail(String email);
    List<StudentMember> findByEmailSentFalse();
    boolean existsByContact(Long contact);

    // Keeps newly registered members who HAVEN'T received cards at the very top
    List<StudentMember> findByEmailSentFalseOrderByRegistrationDateDesc();

    // Multi-tenant version filtered by assigned institutions
    List<StudentMember> findByInstitutionIn(Collection<Institution> institutions);

    // Casts the timestamp to a plain date so time elements (hours/mins/seconds) are ignored
    List<StudentMember> findByEmailSentAtBetweenOrderByEmailSentAtDesc(
            LocalDateTime startDate,
            LocalDateTime endDate);

    // Multi-tenant version filtered by assigned institutions
    List<StudentMember> findByEmailSentAtBetweenAndInstitutionInOrderByEmailSentAtDesc(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Collection<Institution> institutions
    );

    // Query method to display student members registered between a date range
    List<StudentMember> findByRegistrationDateBetweenOrderByRegistrationDateDesc(
            LocalDate startDate,
            LocalDate endDate);

    // Multi-tenant version filtered by assigned institutions
    List<StudentMember> findByRegistrationDateBetweenAndInstitutionInOrderByRegistrationDateDesc(
            LocalDate startDate,
            LocalDate endDate,
            Set<Institution> institutions
    );

    Optional<StudentMember> findByMemberId(@Param("memberId") String memberId);

    // Case-insensitive search across name, email, memberId, or contact
    @Query("SELECT s FROM StudentMember s WHERE " +
            "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.memberId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(s.contact AS string) LIKE CONCAT('%', :query, '%')")
    List<StudentMember> searchMembers(@Param("query") String query);
}