package com.gettimhired.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {

    @Value("${resumeeducationservice.mainapp.host}")
    private String mainAppHost;

    @Value("${resumeeducationservice.userservice.host}")
    private String userServiceHost;

    @Bean
    RestClient resumeSiteRestClient() {
        return RestClient.builder().baseUrl(mainAppHost).build();
    }

    @Bean
    RestClient userServiceRestClient() {
        return RestClient.builder().baseUrl(userServiceHost).build();
    }
}
