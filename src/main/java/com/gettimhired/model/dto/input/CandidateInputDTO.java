package com.gettimhired.model.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CandidateInputDTO(
        String id,
        String userId,
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 1, max = 256, message = "First name must be between 1 and 256 characters")
        String firstName,
        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 1, max = 256, message = "Last name must be between 1 and 256 characters")
        String lastName,
        @Size(max = 4000)
        String summary,
        @Pattern(regexp = "^https://www\\.linkedin\\.com.*", message = "LinkedIn Url must be prefaced with https://www.linkedin.com")
        @Size(max = 2048, message = "Linkedin Url must be between 1 and 2048 characters")
        String linkedInUrl,
        @Pattern(regexp = "^https://github\\.com.*", message = "Github Url must be prefaced with https://github.com")
        @Size(max = 2048, message = "Github Url must be between 1 and 2048 characters")
        String githubUrl
) {
}
