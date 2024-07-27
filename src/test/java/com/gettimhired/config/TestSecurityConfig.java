package com.gettimhired.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    SecurityFilterChain filterChainLocal(HttpSecurity http) throws Exception {
        return http
                .securityMatchers(matchers -> {
                    matchers.requestMatchers("/api/**");
                    matchers.requestMatchers("/graphql");
                })
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(basic -> basic.init(http))
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/api/**").authenticated();
                    authorize.requestMatchers("/graphql").authenticated();
                    authorize.anyRequest().permitAll();
                })
                .userDetailsService(userDetailsService())
                .addFilterBefore(new AuthorizationHeaderFilter(), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(1)
    @Profile("local")
    public SecurityFilterChain filterChainLocalForm(HttpSecurity http) throws Exception {
        return http
                .formLogin(formLogin -> {
                    formLogin.defaultSuccessUrl("/account");
                })
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/account").authenticated();
                    authorize.requestMatchers("/postman").authenticated();
                    authorize.requestMatchers("/swagger-ui/**").authenticated();
                    authorize.anyRequest().permitAll();
                })
                .userDetailsService(userDetailsService())
                .build();
        // ...
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user1")
                .password(passwordEncoderTest().encode("password"))
                .roles("USER")
                .build());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoderTest() {
        return new BCryptPasswordEncoder();
    }
}
