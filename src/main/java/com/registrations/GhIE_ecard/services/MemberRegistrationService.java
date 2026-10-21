package com.registrations.GhIE_ecard.services;
import com.registrations.GhIE_ecard.enums.Certification;
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

    // ... your other methods like calculateExpiryDate() ...

    // 2. The new method
    public byte[] validatePhoto(MultipartFile file) {

        // 3. Packaging the File
        // MultiValueMap represents a standard HTML form submission.
        // HTTP requires files to be sent as "multipart/form-data".
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();

        // We use .getResource() instead of .getBytes().
        // This streams the file over the network chunk-by-chunk,
        // which prevents your server's RAM from crashing if a user uploads a huge 20MB photo.
        bodyMap.add("file", file.getResource());

        // 4. The RestClient Call
        // This is the "fluent API" builder pattern. Read it like a sentence:
        return passportAiClient.post()                      // Make a POST request
                .uri("/validate-photo")                      // Add this to your base URL
                .contentType(MediaType.MULTIPART_FORM_DATA) // Tell FastAPI a file is coming
                .body(bodyMap)                              // Attach the file stream
                .retrieve()                                 // Execute the request
                .body(byte[].class);                        // Convert the FastAPI response directly into a byte array
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
