package com.registrations.GhIE_ecard.models;

import com.registrations.GhIE_ecard.enums.Institution;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "admins")
public class Admin implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role")
    private String role;

    // Multi-Institution Mapping
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "assigned_institutions",
            joinColumns = @JoinColumn(name = "admin_id", referencedColumnName = "admin_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "institution_name")
    private Set<Institution> assignedInstitutions = new HashSet<>();

    // Default Constructor required by JPA
    public Admin() {
    }

    public Admin(Long adminId, String username, String email, String password, String role, Set<Institution> assignedInstitutions) {
        this.adminId = adminId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.assignedInstitutions = assignedInstitutions != null ? assignedInstitutions : new HashSet<>();
    }

    // USER DETAILS METHODS
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null || this.role.isBlank()) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Getters and Setters
    public Long getAdminId() {
        return this.adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<Institution> getAssignedInstitutions() {
        if (this.assignedInstitutions == null) {
            this.assignedInstitutions = new HashSet<>();
        }
        return this.assignedInstitutions;
    }

    public void setAssignedInstitutions(Set<Institution> assignedInstitutions) {
        this.assignedInstitutions = assignedInstitutions;
    }

    // Helper method to add a single institution conveniently
    public void addInstitution(Institution institution) {
        if (this.assignedInstitutions == null) {
            this.assignedInstitutions = new HashSet<>();
        }
        this.assignedInstitutions.add(institution);
    }

    // Helper method to remove a single institution conveniently
    public void removeInstitution(Institution institution) {
        if (this.assignedInstitutions != null) {
            this.assignedInstitutions.remove(institution);
        }
    }
}