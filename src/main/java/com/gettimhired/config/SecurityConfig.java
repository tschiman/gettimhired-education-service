package com.gettimhired.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final FormUserDetailsService formUserDetailsService;
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, FormUserDetailsService formUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
        this.formUserDetailsService = formUserDetailsService;
    }

    @Bean
    @Profile("!local")
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .requiresChannel(channel ->
                        channel.anyRequest().requiresSecure())
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
                })
                .userDetailsService(customUserDetailsService)
                .addFilterBefore(new RequestContextFilter(), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(1)
    @Profile("!local")
    public SecurityFilterChain filterChainForm(HttpSecurity http) throws Exception {
        return http
                .requiresChannel(channel ->
                        channel.anyRequest().requiresSecure())
                .formLogin(formLogin -> {
                    formLogin.defaultSuccessUrl("/account");
                })
                .logout(logout -> {
                    logout.logoutSuccessUrl("/");
                })
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/account").authenticated();
                    authorize.requestMatchers("/postman").authenticated();
                    authorize.requestMatchers("/swagger-ui/**").authenticated();
                    authorize.anyRequest().permitAll();
                })
                .userDetailsService(formUserDetailsService)
                .addFilterBefore(new RequestContextFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(0)
    @Profile("local")
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
                })
                .userDetailsService(customUserDetailsService)
                .addFilterBefore(new RequestContextFilter(), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Order(1)
    @Profile("local")
    public SecurityFilterChain filterChainLocalForm(HttpSecurity http) throws Exception {
        return http
                .formLogin(formLogin -> {
                    formLogin.defaultSuccessUrl("/account");
                    formLogin.loginPage("/login");
                })
                .logout(logout -> {
                    logout.logoutSuccessUrl("/");
                })
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/account").authenticated();
                    authorize.requestMatchers("/postman").authenticated();
                    authorize.requestMatchers("/swagger-ui/**").authenticated();
                    authorize.anyRequest().permitAll();
                })
                .userDetailsService(formUserDetailsService)
                .addFilterBefore(new RequestContextFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
