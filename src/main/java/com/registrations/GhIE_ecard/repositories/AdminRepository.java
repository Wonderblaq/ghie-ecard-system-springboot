package com.registrations.GhIE_ecard.repositories;

import com.registrations.GhIE_ecard.models.Member;
import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends CrudRepository<Member, Long> {

}
