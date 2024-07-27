package com.gettimhired.config;

import com.gettimhired.model.dto.CustomUserDetails;
import com.gettimhired.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FormUserDetailsService implements UserDetailsService {
    private final UserService userService;
    public FormUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        var userOpt = userService.findByEmail(email);

        if (userOpt.isPresent()) {
            return new CustomUserDetails(
                    userOpt.get().email(),
                    userOpt.get().emailPassword(),
                    userOpt.get().roles().stream().map(SimpleGrantedAuthority::new).toList());
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
