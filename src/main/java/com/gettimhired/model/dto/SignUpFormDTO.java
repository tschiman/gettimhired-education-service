package com.gettimhired.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpFormDTO(
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
        String password,
        @NotBlank
        @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
        String passwordCopy
) {
}
