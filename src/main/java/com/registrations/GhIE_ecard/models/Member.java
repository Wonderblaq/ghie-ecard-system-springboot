package com.registrations.GhIE_ecard.models;

import com.registrations.GhIE_ecard.enums.Institution;
import com.registrations.GhIE_ecard.enums.Regions;
import jakarta.persistence.*;

@Entity
@Table(name = "\"Member Info\"")
public class Member{

    // ID Column with a getter and setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long documentId;
    public Long getId(){
        return this.documentId;  // getter for Document id
    }

    public void setId(){
        this.documentId= documentId;  // setter for Document id
    }


    // Full Name Column with a getter and setter
    @Column(name = "\"FULL NAME\"")
    public String fullName;

    public String getFullName() {
        return fullName;  // getter for full name
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;  // setter for full name;
    }


    // Unique ID column with a getter and setter
    @Column(unique = true)
    public String memberId;

    public String getMemberId(){
        return this.memberId; // getter for unique ID
    }
    public void setMemberId(String memberId){
        this.memberId = memberId; // setter for member ID
    }


    // Institution column with a getter and setter
    @Enumerated(EnumType.STRING)
    @Column(name = "INSTITUTION")
    public Institution institution;

    public Institution getInstitution(){
        return this.institution;
    }
    public void setInstitution( Institution institution){
        this.institution = institution;
    }


    // Gender Column
    @Column(name = "GENDER")
    public String gender;

    public String getGender(){
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    // Regions Column
    @Enumerated(EnumType.STRING)
    @Column(name = "REGION")
    public Regions region;

    public Regions getRegion() {
        return this.region;
    }
    public void setRegion(Regions region){
        this.region = region;
    }


    // contact Column with a getter and setter
    @Column(name = "CONTACT")
    public Long contact;

    public Long getContact() {
        return this.contact;  // getter for contact
    }
    public void setContact(long contact) {
        this.contact = contact;  // setter ;
    }


    // Email Column with getter and setter
    @Column(name = "EMAILS")
    public String email;
    public String getEmail(){
        return this.email;
    }

    public void setEmail (String email){
        this.email = email;
    }


    // Column with getter and setter
    @Column(name = "\"PROGRAMME\"")
    public String program;
    public String getProgram(){
        return this.program;

    }

    public void setProgram(String program){
        this.program = program;

    }


    // Column with getter and setter
    @Column(name = "\"PHOTO PATH\"")
    public String photo_url;
    public String getPhoto (){
        return this.photo_url;

    }

    public void set (String photo_url){
        this.photo_url = photo_url;

    }


    // Column with getter and setter
    @Column(name = "\"STUDENT ID\"")
    public String studentNumber;
    public String getStudentNumber (){
        return this.studentNumber;

    }

    public void setStudentNumber (String studentNumber){
        this.studentNumber = studentNumber;

    }


    // Column with getter and setter
    @Column(name = "\"ADMISSION YEAR\"")
    public Integer year;
    public Integer getYear (){
        return this.year;

    }

    public void setYear (Integer year){
        this.year = year;

    }

    @Column(name = "\"EMAIL SENT\"", nullable = false)
    @org.hibernate.annotations.ColumnDefault("false")
    public Boolean emailSent = false;
    public boolean getemailSent(){
        return this.emailSent;
    }
    public void setEmailSent( boolean emailSent){
        this.emailSent = emailSent;
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