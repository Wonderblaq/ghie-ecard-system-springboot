package com.registrations.GhIE_ecard.DTO;
import java.util.List;

public class LoginResponseDTO {
    private String token;
    private String username;
    private String email;
    private String role;
    private List<String> regions;

    public LoginResponseDTO(String token, String username, String email, String role, List<String> regions) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.regions = regions;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<String> getRegions() { return regions; }
    public void setRegions(List<String> regions) { this.regions = regions; }
}