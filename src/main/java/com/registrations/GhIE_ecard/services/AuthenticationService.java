package com.registrations.GhIE_ecard.services;

import com.registrations.GhIE_ecard.DTO.LoginRequestDTO;
import com.registrations.GhIE_ecard.repositories.AdminRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.registrations.GhIE_ecard.models.Admin;


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
        // If the password is wrong, this line THROWS an exception and the code stops here to prevent malicious attempts
        authenticationManager.authenticate(userAuth);

        // Get the full Admin object from DB
        var admin = adminRepository.findByUsername(request.getUsername()).orElseThrow(() ->
                new RuntimeException("User not Found after Authentication!"));
        // create a hashMap to hold other parameters needed for logging into admin controller
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", admin.getRole());
        claims.put("institution", admin.getInstitution().name()); // use .name so Institution bject can be safely parsed into String for JSON to use


        return jwtService.generateToken(claims, admin.getUsername()); // now claims map is added to jwt that will be generated


    }
}
