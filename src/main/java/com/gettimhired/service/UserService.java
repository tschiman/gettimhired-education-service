package com.gettimhired.service;

import com.gettimhired.model.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserService {
    Logger log = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate;

    private final String host;

    private final String username;

    private final String password;

    public UserService(PasswordEncoder passwordEncoder,
                       RestTemplate restTemplate,
                       @Value("${resumesite.userservice.host}") String host,
                       @Value("${resumesite.userservice.username}") String username,
                       @Value("${resumesite.userservice.password}") String password
    ) {
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public Optional<UserDTO> findUserById(String id) {
        HttpEntity<UserDTO> httpEntity = getAuthorization();
        
        var result = restTemplate.exchange(host + "/api/users/" + id + "/id", HttpMethod.GET, httpEntity, UserDTO.class);
        
        if (result.getStatusCode().is2xxSuccessful()) {
            return Optional.ofNullable(result.getBody());
        } else {
            log.error("GET /api/users/{id}/id findUserById failed request id={} httpStatus={}", id, result.getStatusCode());
            return Optional.empty();
        }
    }

    public void createUser(String email, String password) {

        URI uri = UriComponentsBuilder.fromHttpUrl(host + "/api/users")
                .queryParam("email", email)
                .queryParam("password", password)
                .build()
                .toUri();

        var result = restTemplate.exchange(uri, HttpMethod.POST, null, UserDTO.class);

        log.info("POST /api/users createUser email={} status={}", email, result.getStatusCode());
    }

    public Optional<UserDTO> findByEmail(String email) {
        HttpEntity<UserDTO> httpEntity = getAuthorization();

        URI uri = UriComponentsBuilder.fromHttpUrl(host + "/api/users/email")
                .queryParam("email", email)
                .build()
                .toUri();

        var result = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, UserDTO.class);

        if (result.getStatusCode().is2xxSuccessful()) {
            return Optional.ofNullable(result.getBody());
        } else {
            log.error("GET /api/users/email findByEmail failed request email={} httpStatus={}", email, result.getStatusCode());
            return Optional.empty();
        }
    }

    public String generatePassword(UserDTO user) {
        HttpEntity<UserDTO> httpEntity = getAuthorization();

        URI uri = UriComponentsBuilder.fromHttpUrl(host + "/api/users/" + user.email() + "/password")
                .build()
                .toUri();

        var result = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);

        if (result.getStatusCode().is2xxSuccessful()) {
            return result.getBody();
        } else {
            log.error("POST /api/users/{email}/password generatePassword failed request email={} httpStatus={}", user.email(), result.getStatusCode());
            return null;
        }
    }

    private HttpEntity<UserDTO> getAuthorization() {

        var toBase64 = username + ":" + password;
        var authHeader = "Basic " + Base64.getEncoder().encodeToString(toBase64.getBytes(StandardCharsets.UTF_8));

        var httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", authHeader);
        return new HttpEntity<>(httpHeaders);
    }
}
