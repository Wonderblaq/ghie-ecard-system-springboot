package com.registrations.GhIE_ecard.controllers;
import com.registrations.GhIE_ecard.DTO.LoginRequestDTO;
import com.registrations.GhIE_ecard.models.Admin;
import com.registrations.GhIE_ecard.models.AuthenticationResponse;
import com.registrations.GhIE_ecard.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.registrations.GhIE_ecard.repositories.AdminRepository;
import java.util.List;




/**
 * This is an Authentication controller
 * it takes a loginDTO entered by an admin
 * Runs the authenticationService class and methods
 * Verifies Admin trying to sign in
 * Admin can now use the Admin Controller once they are granted access
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private final AuthenticationService authenticationService;
    private final AdminRepository adminRepository;


    public AuthController(AuthenticationService authenticationService, AdminRepository adminRepository){
        this.authenticationService = authenticationService;
        this.adminRepository = adminRepository;
    }

    @GetMapping("/Admin-All")
    public List<Admin> getAdmins(){
      return (List<Admin>)adminRepository.findAll(Sort.by(Sort.Direction.ASC));

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequestDTO request){
        String token = authenticationService.authenticateUser(request);

        // Return the "VIP Pass" to Postman
        return ResponseEntity.ok(new AuthenticationResponse(token));



    }


}
