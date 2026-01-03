package com.registrations.GhIE_ecard.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import com.registrations.GhIE_ecard.models.Member;


public interface  MemberRepository extends CrudRepository<Member, Long>{


    Iterable<Member> findAll(Sort memberId);
}
