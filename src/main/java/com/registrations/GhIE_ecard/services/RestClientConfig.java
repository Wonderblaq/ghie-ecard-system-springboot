package com.registrations.GhIE_ecard.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${api.fastapi.base-url}")
    private String fastApiBaseUrl;

    @Bean
    public RestClient fastApiConfig(){
        return RestClient.builder()
                .baseUrl(fastApiBaseUrl)  // set where to fastapi port runs dynamically
                .build();

    }

}
