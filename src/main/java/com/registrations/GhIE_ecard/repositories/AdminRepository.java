package com.registrations.GhIE_ecard.repositories;

import com.registrations.GhIE_ecard.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Member, Long> {
    List<Member> findByEmailSentFalseOrderByMemberIdAsc();
    List<Member> findByEmailSentFalse();







}
