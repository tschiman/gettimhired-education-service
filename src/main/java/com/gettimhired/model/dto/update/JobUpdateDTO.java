package com.gettimhired.model.dto.update;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gettimhired.model.dto.input.JobInputDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JobUpdateDTO(
        @NotBlank(message = "Company name cannot be blank")
        @Size(min = 1, max = 256, message = "Company name must be between 1 and 256 characters")
        String companyName,
        @NotBlank(message = "Title cannot be blank")
        @Size(min = 1, max = 256, message = "Title must be between 1 and 256 characters")
        String title,
        @NotNull(message = "Start date must be present")
        LocalDate startDate,
        LocalDate endDate,
        List<String> skills,
        List<String> achievements,
        @NotNull(message = "Currently working must have be true or false")
        Boolean currentlyWorking,
        @NotBlank
        @Size(min = 1, max = 1000, message = "Reason for leaving must be between 1 and 1000 characters")
        String reasonForLeaving
) {
    public JobUpdateDTO(JobInputDTO job) {
        this(
                job.companyName(),
                job.title(),
                job.startDate(),
                job.endDate(),
                job.skills(),
                job.achievements(),
                job.currentlyWorking(),
                job.reasonForLeaving()
        );
    }
}
