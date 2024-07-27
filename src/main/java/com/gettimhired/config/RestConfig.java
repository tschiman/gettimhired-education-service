package com.gettimhired.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Value("${resumesite.jobservice.host}")
    private String jobServiceHost;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestClient jobServiceRestClient() {
        return RestClient.builder().baseUrl(jobServiceHost).build();
    }
}
