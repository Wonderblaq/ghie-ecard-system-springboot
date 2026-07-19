package com.registrations.GhIE_ecard.repositories;

import com.registrations.GhIE_ecard.enums.Institution;
import com.registrations.GhIE_ecard.models.StudentMember;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface  MemberRepository extends JpaRepository<StudentMember, Long> {


    List<StudentMember> findAll(Sort memberId);
    boolean existsByEmail(String email);
    List<StudentMember> findByEmailSentFalse();
    boolean existsByContact(Long contact);
    List<StudentMember> findByInstitution(Institution institution);


   //Optional<Member> findById(String memberId);
   // 💡 This @Query bypasses the automatic parser confusion!
   @Query("SELECT m FROM StudentMember m WHERE m.memberId = :memberId")
   Optional<StudentMember> findByMemberId(@Param("memberId") String memberId);
}
