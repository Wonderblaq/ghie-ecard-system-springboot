package com.registrations.GhIE_ecard.services;

import com.registrations.GhIE_ecard.DTO.LoginRequestDTO;
import com.registrations.GhIE_ecard.DTO.LoginResponseDTO;
import com.registrations.GhIE_ecard.models.Admin;
import com.registrations.GhIE_ecard.repositories.AdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AuthenticationService {

    private final AdminRepository adminRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 AdminRepository adminRepository,
                                 JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO authenticateUser(LoginRequestDTO request) {
        log.info("Attempting authentication for username: {}", request.getUsername());

        // 1. Prepare unauthenticated authentication token
        var userAuth = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );

        // 2. Authenticate credentials via AuthenticationManager
        try {
            authenticationManager.authenticate(userAuth);
            log.info("Authentication succeeded for username: {}", request.getUsername());
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for username: {} - Bad Credentials", request.getUsername());
            throw e; // Rethrow to preserve Spring Security exception handling
        }

        // 3. Fetch admin entity from DB
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("Authenticated user record not found in database for username: {}", request.getUsername());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found.");
                });

        // 4. Extract assigned regions
        List<String> regionList = admin.getRegions() != null
                ? admin.getRegions().stream().map(Enum::name).toList()
                : List.of();

        String userRole = admin.getRole();
        log.debug("User details loaded -> Username: {}, Role: {}, Regions Count: {}",
                admin.getUsername(), userRole, regionList.size());

        // 5. Build custom JWT claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userRole);
        claims.put("region", regionList);

        // 6. Generate token
        String token = jwtService.generateToken(claims, admin.getUsername());
        log.info("JWT token successfully generated for user: {}", admin.getUsername());

        // 7. Return populated response DTO
        return new LoginResponseDTO(
                token,
                admin.getUsername(),
                admin.getEmail(),
                userRole,
                regionList
        );
    }
}