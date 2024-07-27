package com.gettimhired.model.dto;

import java.util.List;

public record UserDTO(
        String id,
        String password,
        String email,
        String emailPassword,
        List<String> roles
) {
}
