package com.registrations.GhIE_ecard.services;
import com.fasterxml.jackson.databind.JsonNode;
import com.registrations.GhIE_ecard.enums.Certification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.security.cert.Certificate;
import java.time.*;
import com.registrations.GhIE_ecard.enums.EnrollmentYear;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

/**
 * Extract current DateTime using localDateTime
 * Perform calculations by extracting the year part
 * Add program duration years to the extracted year
 * Add 31st October plus calculated years and set expiry date
 */

@Service
public class MemberRegistrationService {

    private final RestClient passportAiClient;

    // 1. Dependency Injection with @Qualifier
    // This tells Spring: "Go to RestClientConfig and fetch the bean named 'passportAiClient'"
    public MemberRegistrationService(@Qualifier("passportAiClient") RestClient passportAiClient) {
        this.passportAiClient = passportAiClient;
    }

    // Changed return type from byte[] to JsonNode
    public JsonNode validatePassportPhoto(MultipartFile file) {

        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", file.getResource());

        // 1. Fetch the response and parse it as JSON
        JsonNode responseJson = passportAiClient.post()
                .uri("/validate-photo")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(bodyMap)
                .retrieve()
                .body(JsonNode.class);

        // 2. Intercept the logic: If "valid" is false, extract the message and throw a 400 Error
        if (responseJson != null && responseJson.has("valid") && !responseJson.get("valid").asBoolean()) {

            // Extract the specific message like "Face is too small. Move closer or crop tighter."
            String errorMessage = responseJson.has("message")
                    ? responseJson.get("message").asText()
                    : "Photo validation failed.";

            // This forces Spring Boot to return an HTTP 400 Bad Request to the frontend
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        // 3. If valid is true, return the success JSON
        return responseJson;
    }


    public LocalDate calculateExpiryDate(EnrollmentYear enrollmentYear, Certification certification){

        // perform null checks on certification and enrollment years
        if (enrollmentYear == null) {
            throw new IllegalArgumentException("Enrollment Year cannot be null");
        }

        if (certification == null){
            throw new IllegalArgumentException("Certification Type cannot be null");
        }

        // Determine program duration based on certification
        int programDurationYears;
        switch (certification){
            case HND ->
                    programDurationYears = 2;
            case Btech,Bsc ->
                    programDurationYears = 3;
            default ->
                    throw new IllegalArgumentException("Unsupported Certification type " + certification );
        }

        final Month EXPIRY_MONTH = Month.OCTOBER;
        final int EXPIRY_DAY = 31;

        Integer extractedYear = enrollmentYear.getEnrollmentYear();
        int EXPIRY_YEAR = extractedYear + programDurationYears;

        return LocalDate.of(EXPIRY_YEAR,EXPIRY_MONTH,EXPIRY_DAY);


    }


}
