package com.registrations.GhIE_ecard.repositories;

import com.registrations.GhIE_ecard.enums.Institution;
import com.registrations.GhIE_ecard.enums.Regions;
import com.registrations.GhIE_ecard.models.StudentMember;
import jakarta.mail.search.SearchTerm;
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


public interface  MemberRepository extends JpaRepository<StudentMember, Long> {


    List<StudentMember> findAll(Sort memberId);
    boolean existsByEmail(String email);
    List<StudentMember> findByEmailSentFalse();
    boolean existsByContact(Long contact);
    List<StudentMember> findByInstitution(Institution institution);
    List<StudentMember> findByRegion(Regions region);

    // Spring Data automatically translates this into: WHERE region IN ('Greater Accra', 'Ashanti', etc)
    List<StudentMember> findByRegionsIn(Set<Regions> region);


    // Casts the timestamp to a plain date so time elements (hours/mins/seconds) are ignored
    @Query("SELECT s FROM StudentMember s WHERE CAST(s.emailSentAt AS date) = :date")
    List<StudentMember> findByEmailSentAtDate(@Param("date") LocalDate date);

    // Multi-tenant version filtered by regions
    @Query("SELECT s FROM StudentMember s WHERE CAST(s.emailSentAt AS date) = :date AND s.region IN :regions")
    List<StudentMember> findByEmailSentAtDateAndRegionIn(@Param("date") LocalDate date, @Param("regions") java.util.Collection<?> regions);

    // Query method to display student members based on their registration dates
    @Query("SELECT s FROM StudentMember s WHERE CAST(s.registrationDate AS date) = :date")
    List<StudentMember> findByRegistrationDate(@Param("date") LocalDate date);

    // Multi-tenant version filtered by their assigned regions
    @Query("SELECT s FROM StudentMember s WHERE CAST(s.registrationDate AS date) = :date AND s.region IN :regions")
    List<StudentMember> findByRegistrationDateAndRegionIn(
            @Param("date") LocalDate date,
            @Param("regions") Set<Regions> regions
    );




    //Optional<Member> findById(String memberId);
   // 💡 This @Query bypasses the automatic parser confusion!
   @Query("SELECT m FROM StudentMember m WHERE m.memberId = :memberId")
   Optional<StudentMember> findByMemberId(@Param("memberId") String memberId);
    // Case-insensitive search across name, email, memberId, or contact
    @Query("SELECT s FROM StudentMember s WHERE " +
            "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.memberId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(s.contact AS string) LIKE CONCAT('%', :query, '%')")
    List<StudentMember> searchMembers(@Param("query") String query);

}
