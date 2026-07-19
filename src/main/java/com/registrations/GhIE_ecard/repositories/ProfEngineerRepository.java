package com.registrations.GhIE_ecard.repositories;

import com.registrations.GhIE_ecard.models.ProfessionalEngineer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfEngineerRepository extends JpaRepository<ProfessionalEngineer, Long> {

    List<ProfessionalEngineer> findAll(Sort membershipNumber);
    Optional<ProfessionalEngineer> findByEmail(String email);
    Optional<ProfessionalEngineer> findByMembershipNumber(String membershipNumber);
}
