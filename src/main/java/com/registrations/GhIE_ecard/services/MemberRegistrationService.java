package com.registrations.GhIE_ecard.services;
import com.registrations.GhIE_ecard.enums.Certification;
import org.springframework.stereotype.Service;

import java.security.cert.Certificate;
import java.time.*;

import com.registrations.GhIE_ecard.enums.EnrollmentYear;

/**
 * Extract current DateTime using localDateTime
 * Perform calculations by extracting the year part
 * Add program duration years to the extracted year
 * Add 31st October plus calculated years and set expiry date
 */

@Service
public class MemberRegistrationService {


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
