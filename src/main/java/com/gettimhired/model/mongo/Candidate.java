package com.gettimhired.model.mongo;

import com.gettimhired.model.dto.CandidateDTO;
import org.springframework.data.annotation.Id;

import java.util.UUID;

public record Candidate(
        @Id String id,
        String userId,
        String firstName,
        String lastName,
        String summary,
        String linkedInUrl,
        String githubUrl
) {
    public Candidate(String userId, CandidateDTO candidateDTO) {
        this(
                UUID.randomUUID().toString(),
                userId,
                candidateDTO.firstName(),
                candidateDTO.lastName(),
                candidateDTO.summary(),
                candidateDTO.linkedInUrl(),
                candidateDTO.githubUrl()
        );
    }
}
