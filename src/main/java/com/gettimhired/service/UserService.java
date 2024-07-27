package com.gettimhired.service;

import com.gettimhired.config.RequestContextHolder;
import com.gettimhired.model.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

@Service
public class UserService {

    Logger log = LoggerFactory.getLogger(UserService.class);
    private final RestClient userServiceRestClient;

    public UserService(RestClient userServiceRestClient) {
        this.userServiceRestClient = userServiceRestClient;
    }

    public Optional<UserDTO> findUserById(String id) {
        try {
            var result = userServiceRestClient
                    .get()
                    .uri("/api/users/" + id + "/id")
                    .header("Authorization", RequestContextHolder.getHeader())
                    .retrieve()
                    .toEntity(UserDTO.class);
            return Optional.ofNullable(result.getBody());
        } catch (RestClientResponseException e) {
            log.error("GET findUserById id={} httpStatus={}", id, e.getStatusCode(), e);
            return Optional.empty();
        }
    }
}
