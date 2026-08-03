package com.registrations.GhIE_ecard.repositories;

import com.registrations.GhIE_ecard.enums.Institution;
import com.registrations.GhIE_ecard.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import com.registrations.GhIE_ecard.enums.Regions;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUsername(String username);
    List<Admin> findByInstitution(Institution institution);
    List<Admin> findByRegion(Regions region);










}
