package com.registrations.GhIE_ecard.DTO;

import org.springframework.stereotype.Service;

@Service
public class  LoginRequestDTO {
    private String username;
    private String password;
    private String email;
    private String role;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void  setPassword(String password){
        this.password = password;

    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public LoginRequestDTO(String role, String username, String email){
        this.email = email;
        this.role = role;
        this.username =username;
    }
}
