package com.registrations.GhIE_ecard.services;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.UUID;


@Service
public class GenerateID {

    // function to generate unique IDs for all students
    public String generateMemberId(){

        LocalDate today = LocalDate.now();
        int yearNow = today.getYear();
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();


        return "GhIE-"+ yearNow + randomPart;
    }

}
