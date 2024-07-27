package com.gettimhired.service;

import com.gettimhired.TestHelper;
import com.gettimhired.model.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private RestTemplate restTemplate;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void init() {
        restTemplate = Mockito.mock(RestTemplate.class);
        userService = new UserService(passwordEncoder, restTemplate, "http://localhost:8080", "username", "password");
    }

    @Test
    public void testFindUserByUsername() {
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(UserDTO.class)
        )).thenReturn(ResponseEntity.of(Optional.of(new UserDTO(TestHelper.ID, "BARK_PASSWORD", "email", "password", Collections.emptyList()))));

        var userOpt = userService.findUserById("BARK");

        verify(restTemplate, times(1)).exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(UserDTO.class)
        );
        assertTrue(userOpt.isPresent());
    }

    @Test
    public void testCreateUser() {
        var user = new UserDTO(TestHelper.ID, "password", "email", "password2", Collections.emptyList());
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), eq(null), eq(UserDTO.class)))
                .thenReturn(ResponseEntity.of(Optional.of(user)));

        assertDoesNotThrow(() -> userService.createUser("email", "password"));

        verify(restTemplate, times(1)).exchange(any(URI.class), any(HttpMethod.class), eq(null), eq(UserDTO.class));
    }

    @Test
    public void testFindUserByEmail() {
        var user = new UserDTO(TestHelper.ID, "password", "email", "password2", Collections.emptyList());
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(UserDTO.class)))
                .thenReturn(ResponseEntity.of(Optional.of(user)));

        var result = userService.findByEmail("email");

        verify(restTemplate, times(1)).exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(UserDTO.class));
    }

    @Test
    public void testGeneratePassword() {
        var user = new UserDTO(TestHelper.ID, "password", "email", "password2", Collections.emptyList());
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.of(Optional.of("passwordNew")));

       var result = userService.generatePassword(user);

       assertNotNull(result);
       verify(restTemplate, times(1)).exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(String.class));
    }
}