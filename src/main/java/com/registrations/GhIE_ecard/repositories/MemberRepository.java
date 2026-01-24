package com.registrations.GhIE_ecard.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import com.registrations.GhIE_ecard.models.Member;

import java.util.List;
import java.util.Optional;


public interface  MemberRepository extends JpaRepository<Member, Long> {


    List<Member> findAll(Sort memberId);
    Optional<Member> findByEmail(String email);
    List<Member> findByEmailSentFalse();

}
