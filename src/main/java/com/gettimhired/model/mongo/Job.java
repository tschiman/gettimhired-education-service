package com.gettimhired.model.mongo;

import com.gettimhired.model.dto.JobDTO;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Job(
    @Id String id,
    String userId,
    String candidateId,
    String companyName,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    List<String> skills,
    List<String> achievements,
    Boolean currentlyWorking,
    String reasonForLeaving

) {
    public Job(String userId, String candidateId, JobDTO jobDto) {
        this(
                UUID.randomUUID().toString(),
                userId,
                candidateId,
                jobDto.companyName(),
                jobDto.title(),
                jobDto.startDate(),
                jobDto.endDate(),
                jobDto.skills(),
                jobDto.achievements(),
                jobDto.currentlyWorking(),
                jobDto.reasonForLeaving()
        );
    }
}
