package com.registrations.GhIE_ecard.emailServices;

import com.registrations.GhIE_ecard.models.StudentMember;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    // method to send registration rejection email


    // Method to send simple email

    String sendSimpleMail(EmailDetails details);


    Boolean sendRegistrationRejection(StudentMember memberId, String reason);

    // Method to send email with attachment
    String sendMailWithAttachment(EmailDetails details);




}