package com.registrations.GhIE_ecard.emailServices;

import com.registrations.GhIE_ecard.emailServices.EmailDetails;
import java.io.File;
import java.util.Optional;

import com.registrations.GhIE_ecard.models.StudentMember;
import com.registrations.GhIE_ecard.repositories.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Service
public abstract class EmailServiceImpl implements EmailService {
    public final SpringTemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    private final MemberRepository memberRepository;


    public EmailServiceImpl( SpringTemplateEngine templateEngine,
                             MemberRepository memberRepository,JavaMailSender javaMailSender){
        this.templateEngine = templateEngine;
        this.memberRepository = memberRepository;
        this.javaMailSender = javaMailSender;
    }

    @Value("${spring.mail.username}")
    private String sender;

    Context context = new Context();



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

            return "Mail Sent Successfully";

        } catch (Exception e) {

            return "Error while sending mail";
        }
    }
    public String sendRegistrationRejection(StudentMember member){
        String recipient = member.getEmail();




        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

//            mailMessage.setFrom(sender);
//            mailMessage.setTo(recipient);
//            mailMessage.setText();

            javaMailSender.send(mailMessage);
            return "Mail Sent succesfully";

        }
        catch (Exception e){
            return "Error while sending mail";
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