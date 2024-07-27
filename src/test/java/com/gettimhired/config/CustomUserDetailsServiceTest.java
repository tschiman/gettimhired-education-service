package com.gettimhired.config;

import com.gettimhired.TestHelper;
import com.gettimhired.model.dto.UserDTO;
import com.gettimhired.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.mock;

class CustomUserDetailsServiceTest {

    private CustomUserDetailsService customUserDetailsService;
    private UserService userService;

    @BeforeEach
    public void init() {
        userService = mock(UserService.class);
        customUserDetailsService = new CustomUserDetailsService(userService);
    }

    @Test
    public void testUserDetailsServiceHappy() {
        var userOpt = Optional.of(new UserDTO(TestHelper.ID, "BARK_PASSWORD", "email", "password", Collections.emptyList()));
        Mockito.when(userService.findUserById("BARK")).thenReturn(userOpt);

        var userDetails = customUserDetailsService.loadUserByUsername("BARK");

        Mockito.verify(userService, Mockito.times(1)).findUserById("BARK");
        Assertions.assertEquals(TestHelper.ID, userDetails.getUsername());
        Assertions.assertEquals("BARK_PASSWORD", userDetails.getPassword());
        Assertions.assertNotNull(userDetails.getAuthorities());
        Assertions.assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test
    public void testUserDetailsServiceUserNotFound() {
        Optional<UserDTO> userOpt = Optional.empty();
        Mockito.when(userService.findUserById("BARK")).thenReturn(userOpt);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("BARK"));

        Mockito.verify(userService, Mockito.times(1)).findUserById("BARK");
    }

}