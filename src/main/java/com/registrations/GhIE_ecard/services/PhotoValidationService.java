package com.registrations.GhIE_ecard.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class PhotoValidationService {

    public byte[] processAndForwardPhoto(MultipartFile file) {
        RestTemplate restTemplate = new RestTemplate();

        // The internal Docker URL for your Python FastAPI service
        String pythonAiUrl = "http://passport-photo-service:8001/crop-passport";

        try {
            // 1. Package the file into a multipart form data request
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            // .getResource() is the trick to pass the file stream cleanly to RestTemplate
            body.add("file", file.getResource());

            // 2. Set the correct headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 3. Fire the request to FastAPI and expect a byte array back
            ResponseEntity<byte[]> response = restTemplate.postForEntity(pythonAiUrl, requestEntity, byte[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("AI Photo Validation Failed: Invalid response from ML service");
            }
        } catch (Exception e) {
            // Handle exceptions (e.g., FastAPI is down, or FastAPI returned a 400 Bad Request because no face was found)
            throw new RuntimeException("Photo processing failed: " + e.getMessage());
        }
    }
}