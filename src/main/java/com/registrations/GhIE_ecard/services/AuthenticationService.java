package com.registrations.GhIE_ecard.services;

import com.registrations.GhIE_ecard.DTO.LoginRequestDTO;
import com.registrations.GhIE_ecard.repositories.AdminRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


/**
 * This service is the "Executioner." It takes the LoginRequestDTO,
 * verifies the credentials against the database,
 * and uses the JwtService to build the token
 */
@Service
public class AuthenticationService {

    private final AdminRepository adminRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationService(AuthenticationManager authenticationManager,
    AdminRepository adminRepository,JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
    }

    public String authenticateUser (LoginRequestDTO request) {
        var userAuth = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );
        // Hand the envelope to the Executioner
        // If the password is wrong, this line THROWS an exception and the code stops here.
        authenticationManager.authenticate(userAuth);

        // Get the full Admin object from DB
        var admin = adminRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                new RuntimeException("User not Found after Authentication!"));


        return jwtService.generateToken(admin.getUsername());


    }
}
