package com.registrations.GhIE_ecard.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.registrations.GhIE_ecard.enums.EnrollmentYear;
import com.registrations.GhIE_ecard.enums.Institution;
import com.registrations.GhIE_ecard.enums.Regions;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "member_info")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @Column(name = "fullName")
    private String fullName;

    @Column(name = "memberId", unique = true)
    private String memberId;

    @Enumerated(EnumType.STRING)
    private Institution institution;

    @Column(name = "gender")
    private String gender;

    @Enumerated(EnumType.STRING)
    private Regions region;

    @Column(name = "emailSentAt")
    private LocalDateTime emailSentAt;

    @Enumerated(EnumType.STRING)
    private EnrollmentYear enrollmentYear;

    @Column(unique = true)
    private Long contact;

    @Column(unique = true)
    private String email;

    private String program;
    private String photoUrl;
    private String studentNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private Boolean emailSent = false;

    public Member() {} // REQUIRED BY JPA

    // --- getters & setters

    public Long getDocumentId() {
        return documentId;
    }
    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public Institution getInstitution() { return institution; }
    public void setInstitution(Institution institution) { this.institution = institution; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Regions getRegion() { return region; }
    public void setRegion(Regions region) { this.region = region; }

    public Long getContact() { return contact; }
    public void setContact(Long contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }


    public Boolean getEmailSent() { return emailSent; }
    public void setEmailSent(Boolean emailSent) { this.emailSent = emailSent; }

    public LocalDateTime getEmailSentAt(){
        return emailSentAt;
    }
    public void setEmailSentAt(LocalDateTime emailSentAt){
        this.emailSentAt = emailSentAt;
    }
    public EnrollmentYear getEnrollmentYear(){
        return enrollmentYear;
    }
    public void setEnrollmentYear(EnrollmentYear enrollmentYear){
        this.enrollmentYear = enrollmentYear;
    }
    public LocalDate getRegistrationDate(){
        return this.registrationDate;
    }
    public void setRegistrationDate(LocalDate registrationDate){
        this.registrationDate = registrationDate;
    }

    public LocalDate getExpiryDate(){
        return expiryDate;
    }
    public void setExpiryDate(LocalDate expiryDate){
        this.expiryDate = expiryDate;
    }
}



/* RUN THIS PROJECT WITH THE PORT NUMBER 8080:
E.G curl http://localhost:8080/ ,
and add base paths(endpoint) name
 with appropriate request method

 // NOTE : PROJECT GENERATES AN ERROR UPON RUNNING WITH SPRING-BOOT:RUN,
          this is not a logic,syntax or runtime error, postgres database password
          is embedded in the OS at the application.java file,
          Ask Developer to grant you password
 */