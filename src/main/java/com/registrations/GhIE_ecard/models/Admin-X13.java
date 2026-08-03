package com.registrations.GhIE_ecard.models;
import com.registrations.GhIE_ecard.enums.Regions;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
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
    private Long adminId;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;


    /* Added a new column named 'institution' to enable role-based access.
   This ensures that campus coordinators can log in to the system
   but will only be able to view data specific to their institution. */
//    @Enumerated(EnumType.STRING)
//    @Column(name = "institution")
//    private Institution institution;

    // Tenancy change: Allows a single Admin to manage MULTIPLE regions
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "admin_regions",
            joinColumns = @JoinColumn(name = "admin_id") // Matches primary key column of Admin table
    )
    @Enumerated(EnumType.STRING) // Saves Enum names like 'GREATER_ACCRA' instead of numbers (0, 1)
    @Column(name = "region_name")
    private Set<Regions> regions = new HashSet<>(); // Must be a Set<Regions>

// USER DETAILS METHOD
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Implements ROLE BASED ACCESS FOR admins
        // Ensure every Admin gets the "ROLE_" authority plus their role
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }



    @Override
    public boolean isAccountNonExpired() { // Account never expires for now
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
    public boolean isEnabled(){
        return true; //Ensure account is always active
    }

    // Getters and Setters
    public Long getAdminId(){
        return this.adminId;
    }
    public void setAdminId(Long adminId){
        this.adminId = adminId;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public String getRole(){ return this.role; }

    public void setRole(String role) { this.role = role; }

    public Set<Regions> getRegions(){
        return this.regions;
    }

    public void setRegions(Set<Regions> regions) {
        this.regions = regions;
    }

    // Helper method to add a single region conveniently
    public void addRegion(Regions regions){
        if(this.regions == null){
            this.regions = new HashSet<>();
        }
        this.regions.add(regions);

    }

    // Helper method to remove a single region conveniently
    public void removeRegion(Regions regions){
        if(this.regions != null){
            this.regions.remove(regions);
        }
    }



}
