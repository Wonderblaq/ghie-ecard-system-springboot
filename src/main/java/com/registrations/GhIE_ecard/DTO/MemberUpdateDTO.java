package com.registrations.GhIE_ecard.DTO;

import org.springframework.stereotype.Service;

@Service
public class MemberUpdateDTO {
    public String newEmail;
    public Long newContact;

    public Long getNewContact() {
        return newContact;
    }

    public void setNewContact(Long newContact) {
        this.newContact = newContact;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
