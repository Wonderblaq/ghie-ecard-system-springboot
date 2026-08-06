package com.registrations.GhIE_ecard.DTO;

import com.registrations.GhIE_ecard.enums.Regions;

public class AdminResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String role; //
    private Regions region;

    // Getters and Setters / Constructor
    public AdminResponseDTO(String username, String role, String email){
        this.email = email;
        this.role = role;
        this.username = username;

    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public Regions getRegion() {
        return region;
    }

    public void setRegion(Regions region) {
        this.region = region;
    }
}
