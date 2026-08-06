package com.registrations.GhIE_ecard.services;

import com.registrations.GhIE_ecard.DTO.LoginRequestDTO;
import com.registrations.GhIE_ecard.models.Admin;
import com.registrations.GhIE_ecard.repositories.AdminRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationService {

    private final AdminRepository adminRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoginRequestDTO loginRequestDTO;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 AdminRepository adminRepository,
                                 JwtService jwtService, LoginRequestDTO loginRequestDTO) {
        this.authenticationManager = authenticationManager;
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
        this.loginRequestDTO = loginRequestDTO;
    }

    public String authenticateUser(LoginRequestDTO request) {
        // 1. Hand envelope to Spring Security AuthenticationManager
        var userAuth = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );

        // Throws BadCredentialsException if username/password is wrong (triggers 401)
        authenticationManager.authenticate(userAuth);

        // 2. Fetch admin user from DB
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User profile not found."));

        // 3. Convert Set<Regions> to a clean list of string names for JWT payload
        List<String> regionList = admin.getRegions() != null
                ? admin.getRegions().stream().map(Enum::name).toList()
                : List.of();

        // 4. Build custom JWT claims and Get the roles of the loggedInAdmin
        Map<String, Object> claims = new HashMap<>();
        String userRole = userAuth.getAuthorities().stream()
                .findFirst()
                        .map(GrantedAuthority::getAuthority)
                                .orElse("ADMIN");
        // If Spring added "ROLE_" to the front (e.g. "ROLE_SUPER_ADMIN"), strip it off so it becomes "SUPER_ADMIN"
        if (userRole.startsWith("ROLE_")){
           userRole = userRole.replace("ROLE_","");
        }
        claims.put("ROLE",userRole);
        claims.put("region", regionList); // Multi-region support string array


        // 5. Generate and return token
        return jwtService.generateToken(claims, admin.getUsername());
    }
}