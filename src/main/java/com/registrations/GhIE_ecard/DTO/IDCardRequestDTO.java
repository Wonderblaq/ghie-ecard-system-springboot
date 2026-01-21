package com.registrations.GhIE_ecard.services;

import com.registrations.GhIE_ecard.enums.Institution;

public class IDCardRequestDTO {


    private String fullName;
    private String memberId;
    private Institution institution;
    private String gender;
    private String email;
    private String registrationDate;
    private String expiryYear;
    private String photoUrl;


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

    public String getRegistrationDate() {
        return registrationDate;
    }
    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
    public String getExpiryYear(){
        return expiryYear;
    }
    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
