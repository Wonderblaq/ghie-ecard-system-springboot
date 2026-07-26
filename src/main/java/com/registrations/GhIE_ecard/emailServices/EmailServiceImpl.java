package com.registrations.GhIE_ecard.emailServices;

import java.io.File;

import com.registrations.GhIE_ecard.models.StudentMember;
import com.registrations.GhIE_ecard.repositories.*;
import com.registrations.GhIE_ecard.services.CardDispatchService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    //Dependency injection
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private final MemberRepository memberRepository;

    public EmailServiceImpl( SpringTemplateEngine templateEngine,
                             MemberRepository memberRepository,JavaMailSender javaMailSender){
        this.templateEngine = templateEngine;
        this.memberRepository = memberRepository;
        this.javaMailSender = javaMailSender;
    }

    @Value("${mail.sender}")
    private String sender;
    // Send simple mail
    public String sendSimpleMail(EmailDetails details) {

        try {

            SimpleMailMessage mailMessage =
                    new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);
            log.info("Using sender email: {}", sender);

            return "Mail Sent Successfully";

        } catch (Exception e) {
            log.error("Using sender email: {}", sender);

            return "Error while sending mail";
        }
    }

    public Boolean sendRegistrationRejection(StudentMember member, String reason){
        String recipient = member.getEmail();
        Context context = new Context();
        // Fill the context with variables
        context.setVariable("fullName", member.getFullName());
        context.setVariable("reason", reason);
        // process the template
        String html = templateEngine.process("registration-rejection", context);

        try {
            // Instantiate a new MimeMessage object and wrap our mimeMessage object in a MimeMessage helper class
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setSubject("Action Required: Re-upload Your Passport Photo");
            helper.setTo(recipient);
            helper.setFrom(sender);
            helper.setText(html, true); // set Text to True so it will be sent as html produced by ThymeLeaf
            javaMailSender.send(mimeMessage);

            return true;

        }
        catch (Exception e){

            log.error("Failed to send email to {}",recipient, e);
            return false;
        }

    }

    // Send mail with attachment
    public String sendMailWithAttachment(
            EmailDetails details) {

        MimeMessage mimeMessage =
                javaMailSender.createMimeMessage();

        MimeMessageHelper helper;

        try {

            helper =
                    new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setText(details.getMsgBody());
            helper.setSubject(details.getSubject());

            FileSystemResource file =
                    new FileSystemResource(
                            new File(details.getAttachment()));

            helper.addAttachment(
                    file.getFilename(), file);

            javaMailSender.send(mimeMessage);

            return "Mail Sent Successfully";

        } catch (MessagingException e) {

            return "Error while sending mail";
        }
    }
}