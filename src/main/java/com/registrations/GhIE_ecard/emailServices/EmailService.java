package com.registrations.GhIE_ecard.emailServices;

import com.registrations.GhIE_ecard.emailServices.EmailDetails;
import com.registrations.GhIE_ecard.models.StudentMember;
import com.registrations.GhIE_ecard.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface EmailService {
    // method to send registration rejection email


    // Method to send simple email

    String sendSimpleMail(EmailDetails details);

    // Method to send email with attachment
    String sendMailWithAttachment(EmailDetails details);
    String sendRegistrationRejection(EmailDetails details);


}