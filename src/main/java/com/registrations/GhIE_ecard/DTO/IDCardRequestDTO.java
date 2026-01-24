package com.registrations.GhIE_ecard.DTO;

import com.registrations.GhIE_ecard.enums.Institution;

import com.registrations.GhIE_ecard.enums.Regions;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class IDCardRequestDTO {


    private String fullName;
    private String memberId;
    private Institution institution;
    private String gender;
    public Regions region;
    private String email;
    private LocalDate registrationDate;
    private LocalDate expiryDate;
    private String photoUrl;
    private LocalDateTime emailSentAt;


    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMemberId(){
        return memberId;
    }
    public void setMemberId(String memberId){
        this.memberId = memberId;
    }

    public Institution getInstitution() {
        return institution;
    }
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public String getGender(){
        return this.gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    public LocalDate getExpiryDate(){
        return expiryDate;
    }
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public LocalDateTime getEmailSentAt(){
        return emailSentAt;
    }
    public void setEmailSentAt(LocalDateTime emailSentAt){
        this.emailSentAt = emailSentAt;
    }
}
