package com.registrations.GhIE_ecard.services;

import com.registrations.GhIE_ecard.DTO.IDCardRequestDTO;
import com.registrations.GhIE_ecard.models.Member;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CardDispatchService {
    private final RestClient restClient;

    public CardDispatchService(RestClient restClient) {
        this.restClient = restClient;
    }
    public String callFastApi(Member member){
        IDCardRequestDTO data = new IDCardRequestDTO();
        data.setFullName(member.getFullName());
        data.setMemberId(member.getMemberId());
        data.setEmail(member.getEmail());
        data.setGender(member.getGender());
        data.setInstitution(member.getInstitution());
        data.setRegistrationDate(String.valueOf(member.getRegistrationDate()));
        data.setExpiryYear(String.valueOf(member.getExpiryDate()));
        data.setPhotoUrl(member.getPhotoUrl());



    }
}
