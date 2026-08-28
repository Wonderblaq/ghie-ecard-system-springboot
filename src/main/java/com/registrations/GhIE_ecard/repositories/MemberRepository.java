package com.registrations.GhIE_ecard.repositories;

import com.registrations.GhIE_ecard.enums.Regions;
import com.registrations.GhIE_ecard.models.StudentMember;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MemberRepository extends JpaRepository<StudentMember, Long> {

    List<StudentMember> findAll(Sort memberId);
    boolean existsByEmail(String email);
    List<StudentMember> findByEmailSentFalse();
    boolean existsByContact(Long contact);


    // FIX: Changed findByRegionsIn -> findByRegionIn
    List<StudentMember> findByRegionIn(Collection<Regions> regions);

    // Casts the timestamp to a plain date so time elements (hours/mins/seconds) are ignored
   // @Query("SELECT s FROM StudentMember s WHERE CAST(s.emailSentAt AS date)  >= :date ORDER BY s.emailSentAt DESC")
    List<StudentMember> findByEmailSentAtGreaterThanEqualOrderByEmailSentAtDesc(@Param("date") LocalDate date);

    // Multi-tenant version filtered by regions
    //@Query("SELECT s FROM StudentMember s WHERE CAST(s.emailSentAt AS date) >= :date AND s.region IN :regions ORDER BY s.emailSentAt DESC")
    List<StudentMember> findByEmailSentAtGreaterThanEqualAndRegionInOrderByEmailSentAtDesc(@Param("date") LocalDate date,
                                                                                           @Param("regions") Collection<Regions> regions);

    // Query method to display student members registered from a given date onward
    //@Query("SELECT s FROM StudentMember s WHERE CAST(s.registrationDate AS date) >= :date ORDER BY s.registrationDate DESC")
    List<StudentMember> findByRegistrationDateGreaterThanEqualOrderByRegistrationDateDesc(@Param("date") LocalDate date);

    // Multi-tenant version filtered by their assigned regions
    @Query("SELECT s FROM StudentMember s WHERE CAST(s.registrationDate AS date) >= :date AND s.region IN :regions ORDER BY s.registrationDate DESC")
    List<StudentMember> findByRegistrationDateGreaterThanEqualAndRegionInOrderByRegistrationDateDesc(
            @Param("date") LocalDate date,
            @Param("regions") Set<Regions> regions
    );

    //@Query("SELECT m FROM StudentMember m WHERE m.memberId = :memberId")
    Optional<StudentMember> findByMemberId(@Param("memberId") String memberId);

    // Case-insensitive search across name, email, memberId, or contact
    @Query("SELECT s FROM StudentMember s WHERE " +
            "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.memberId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(s.contact AS string) LIKE CONCAT('%', :query, '%')")
    List<StudentMember> searchMembers(@Param("query") String query);
}