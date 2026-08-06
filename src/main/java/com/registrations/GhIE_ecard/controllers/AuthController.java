package com.registrations.GhIE_ecard.controllers;

import com.registrations.GhIE_ecard.DTO.LoginRequestDTO;
import com.registrations.GhIE_ecard.DTO.LoginResponseDTO;
import com.registrations.GhIE_ecard.services.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    // ONLY inject Spring Services/Components here—NEVER DTOs or Entities!
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginAdmin(@RequestBody LoginRequestDTO request) {
        // 'request' is automatically instantiated by Spring MVC from incoming JSON payload
        LoginResponseDTO response = authenticationService.authenticateUser(request);
        return ResponseEntity.ok(response);
    }
}