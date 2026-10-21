package com.registrations.GhIE_ecard.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${api.fastapi.base-url}")
    private String fastApiBaseUrl;

    // Inject the new URL
    @Value("${api.passport-ai.base-url}")
    private String passportAiBaseUrl;

    // Existing Card Client
    @Bean
    public RestClient fastApiConfig(){
        return RestClient.builder()
                .baseUrl(fastApiBaseUrl)
                .build();
    }

    // NEW Passport AI Client
    @Bean
    public RestClient passportAiClient(){
        return RestClient.builder()
                .baseUrl(passportAiBaseUrl)
                .build();
    }
}