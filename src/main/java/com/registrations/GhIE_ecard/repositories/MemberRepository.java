package com.registrations.GhIE_ecard.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import com.registrations.GhIE_ecard.models.Member;

import java.util.List;


public interface  MemberRepository extends JpaRepository<Member, Long> {


    List<Member> findAll(Sort memberId);
    boolean existsByEmail(String email);
    List<Member> findByEmailSentFalse();
    boolean existsByContact(Long contact);

}
