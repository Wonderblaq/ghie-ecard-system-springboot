package com.registrations.GhIE_ecard.repositories;

import com.registrations.GhIE_ecard.models.Member;
import org.apache.catalina.LifecycleState;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends CrudRepository<Member, Long> {
    List<Member> findByEmailSentFalseOrderByMemberIdAsc();





}
