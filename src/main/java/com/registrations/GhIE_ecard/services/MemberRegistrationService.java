package com.registrations.GhIE_ecard.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.*;
import java.sql.Date;
import com.registrations.GhIE_ecard.models.Member;
import com.registrations.GhIE_ecard.enums.EnrollmentYear;
import com.registrations.GhIE_ecard.repositories.MemberRepository;

/**
 * Extract current DateTime using localDateTime
 * Perform calculations by extracting the year part
 * Add 4 years to the extracted year
 * Add 31st October plus calculated years and set expiry date
 */
@Service
public class MemberRegistrationService {
    public LocalDate calculateExpiryDate(EnrollmentYear enrollmentYear){
        // If enrollmentYear is null, default to current year or throw a specific error
        if (enrollmentYear == null) {
            throw new IllegalArgumentException("Enrollment Year cannot be null");
        }
        final Month EXPIRY_MONTH = Month.OCTOBER;
        final int EXPIRY_DAY = 31;

        Integer extractedYear = enrollmentYear.getEnrollmentYear();
        int EXPIRY_YEAR = extractedYear + 4;

        return LocalDate.of(EXPIRY_YEAR,EXPIRY_MONTH,EXPIRY_DAY);


    }


}
