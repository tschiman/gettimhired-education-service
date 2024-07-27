package com.gettimhired.model.dto.update;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gettimhired.model.dto.input.EducationInputDTO;
import com.gettimhired.model.mongo.EducationLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EducationUpdateDTO(
        @Size(min = 1, max = 256, message = "Name of school can be between 1 and 256 characters")
        String name,
        LocalDate startDate,
        LocalDate endDate,
        Boolean graduated,
        @Size(min = 1, max = 256, message = "Area of Study can be between 1 and 256 characters")
        String areaOfStudy,
        @NotNull
        EducationLevel educationLevel
) {
    public EducationUpdateDTO(EducationInputDTO education) {
        this(
                education.name(),
                education.startDate(),
                education.endDate(),
                education.graduated(),
                education.areaOfStudy(),
                education.educationLevel()
        );
    }
}
