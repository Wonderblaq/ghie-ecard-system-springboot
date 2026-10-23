package com.registrations.GhIE_ecard.DTO;

import java.util.List;

public class LoginResponseDTO {
    private String token;
    private String username;
    private String email;
    private String role;
    private List<String> assignedInstitutions;

    // Default Constructor (Required by Jackson for JSON processing)
    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, String username, String email, String role, List<String> assignedInstitutions) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.assignedInstitutions = assignedInstitutions;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getAssignedInstitutions() {
        return assignedInstitutions;
    }
    public void setAssignedInstitutions(List<String> assignedInstitutions) {
        this.assignedInstitutions = assignedInstitutions;
    }
}