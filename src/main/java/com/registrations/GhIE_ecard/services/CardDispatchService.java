package com.registrations.GhIE_ecard.services;

import com.registrations.GhIE_ecard.DTO.IDCardRequestDTO;
import com.registrations.GhIE_ecard.models.CardProcessingResult;
import com.registrations.GhIE_ecard.models.Member;
import com.registrations.GhIE_ecard.repositories.AdminRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CardDispatchService {
    private final RestClient restClient;
    private final AdminRepository adminRepository;

    public CardDispatchService(RestClient restClient, AdminRepository adminRepository) {
        this.restClient = restClient;
        this.adminRepository = adminRepository;
    }
    public boolean callFastApi(Member member){
        // Creating the DTO with required member data for card creation
        IDCardRequestDTO data = new IDCardRequestDTO();
        data.setFullName(member.getFullName());
        data.setMemberId(member.getMemberId());
        data.setEmail(member.getEmail() != null ? member.getEmail().toLowerCase(): null);
        data.setGender(member.getGender());
        data.setInstitution(member.getInstitution());
        data.setRegistrationDate(LocalDate.parse(member.getRegistrationDate().toString()));
        data.setExpiryDate(member.getExpiryDate() != null ? LocalDate.parse(member.getExpiryDate().toString()) : null);
        data.setPhotoUrl(member.getPhotoUrl());

        // Send POST request to FastAPI
        try {
            // 2. Perform the request and capture the response
            String response = restClient.post()
                    .uri("/create_and_send_card")
                    .body(data)
                    .retrieve()
                    // NEW: This handler triggers if FastAPI sends a 422 or other 4xx error
                    .onStatus(HttpStatusCode::is4xxClientError, (request, res) -> {
                        // Extract the detailed validation message from FastAPI
                        String errorBody = new String(res.getBody().readAllBytes());
                        System.out.println("FastAPI Error Detail: " + errorBody);

                        // Stop execution because the request was rejected
                        throw new RuntimeException("FastAPI Validation Failed: " + errorBody);
                    })
                    .body(String.class);

            return true; // Successful path

        } catch (Exception e) {
            // 3. Handle network failures or our custom RuntimeException above
            e.printStackTrace();
            return false;
        }
     // Getting responseBody as string

    }
    public CardProcessingResult processPendingCards() {
        List<Member> pendingMembers = adminRepository.findByEmailSentFalse();
        int success = 0;
        int failure = 0;
        for (Member member : pendingMembers) {
            System.out.println("Calling FastApi for member" + " " + member.getMemberId());
            boolean sent = callFastApi(member);
            if (sent){
                member.setEmailSent(true);
                success++;
                member.setEmailSentAt(LocalDateTime.now());
                adminRepository.save(member);
            }
            else {
                failure++;
                System.out.println("Failed for " + member.getFullName());
                member.setEmailSent(false);
            }

        }
        return new CardProcessingResult(success, failure);
    }
}
