package com.registrations.GhIE_ecard.services;

import com.registrations.GhIE_ecard.DTO.IDCardRequestDTO;
import com.registrations.GhIE_ecard.models.Member;
import com.registrations.GhIE_ecard.repositories.AdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;


@Service
public class FastApiClientService {
    private static final Logger log = LoggerFactory.getLogger(CardDispatchService.class);
    private final RestClient restClient;


    public FastApiClientService(RestClient restClient) {
        this.restClient = restClient;

    }

    @Async("taskExecutor")  // tells Spring to use my specific pool of 5 threads
    public CompletableFuture<Boolean> callFastApi(Member member){
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
                        log.error("FastAPI Error Detail: {}", errorBody);

                        // Stop execution because the request was rejected
                        throw new RuntimeException("FastAPI Validation Failed: " + errorBody);
                    })
                    .body(String.class);

            log.info("Card successfully dispatched for : {}", member.getMemberId());
            return CompletableFuture.completedFuture(true); // Successful path

        } catch (Exception e) {
            // 3. Handle network failures or our custom RuntimeException above
            log.error("Failed to Dispatch card: {}", e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
        // Getting responseBody as string

    }
}
