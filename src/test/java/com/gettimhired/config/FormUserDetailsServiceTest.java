package com.gettimhired.config;

import com.gettimhired.model.dto.UserDTO;
import com.gettimhired.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class FormUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private FormUserDetailsService formUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        String id = "id";
        var user = new UserDTO(id, "password123", "email", "password", List.of("ROLE_USER"));
        when(userService.findByEmail(id)).thenReturn(Optional.of(user));

        UserDetails userDetails = formUserDetailsService.loadUserByUsername(id);

        assertEquals("email", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String email = "test@example.com";
        when(userService.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            formUserDetailsService.loadUserByUsername(email);
        });
    }
}