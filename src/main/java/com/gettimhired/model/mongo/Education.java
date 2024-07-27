package com.gettimhired.model.mongo;

import com.gettimhired.model.dto.EducationDTO;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.UUID;

public record Education(
        @Id String id,
        String userId,
        String candidateId,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        Boolean graduated,
        String areaOfStudy,
        EducationLevel educationLevel
) {
    public Education(String userId, String candidateId, EducationDTO educationDTO) {
        this(
                UUID.randomUUID().toString(),
                userId,
                candidateId,
                educationDTO.name(),
                educationDTO.startDate(),
                educationDTO.endDate(),
                educationDTO.graduated(),
                educationDTO.areaOfStudy(),
                educationDTO.educationLevel()
        );
    }
}
