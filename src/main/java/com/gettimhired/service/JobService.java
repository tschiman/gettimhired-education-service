package com.gettimhired.service;


import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.JobDTO;
import com.gettimhired.model.dto.update.JobUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    Logger log = LoggerFactory.getLogger(JobService.class);
    private final RestClient jobServiceRestClient;
    private final String username;

    private final String password;

    public JobService(
            RestClient jobServiceRestClient,
            @Value("${resumesite.userservice.username}") String username,
            @Value("${resumesite.userservice.password}") String password) {
        this.jobServiceRestClient = jobServiceRestClient;
        this.username = username;
        this.password = password;
    }

    public List<JobDTO> findAllJobsForUserAndCandidateId(String userId, String candidateId) {
        try {
            var jobsResponse = jobServiceRestClient.get()
                    .uri(builder -> builder
                            .path("/api/candidates/" + candidateId + "/jobs")
                            .queryParam("userId", userId)
                            .build()
                    )
                    .header("Authorization", makeBasicToken(username, password))
                    .retrieve()
                    .toEntity(JobDTO[].class);
            return jobsResponse.getBody() == null ? Collections.emptyList() : List.of(jobsResponse.getBody());
        }catch (RestClientResponseException e) {
            log.error("GET /api/candidates/" + candidateId + "/jobs findAllJobsForUserAndCandidateId userId={} httpStatus={}", userId, e.getStatusCode(), e);
            return Collections.emptyList();
        }
    }

    public Optional<JobDTO> findJobByIdAndUserId(String id, String userId, String candidateId) {
        try {
            var jobResponse = jobServiceRestClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/candidates/" + candidateId + "/jobs/" + id)
                            .queryParam("userId", userId)
                            .build()
                    )
                    .header("Authorization", makeBasicToken(username, password))
                    .retrieve()
                    .toEntity(JobDTO.class);
            return jobResponse.getBody() == null ? Optional.empty() : Optional.of(jobResponse.getBody());
        } catch (RestClientResponseException e) {
            log.error("GET /api/candidates/" + candidateId + "/jobs/" + id + " findJobByIdAndUserId userId={} httpStatus={}", userId, e.getStatusCode(), e);
            return Optional.empty();
        }
    }

    public Optional<JobDTO> createJob(String userId, String candidateId, JobDTO jobDto) {
        try {
            var createJobResponse = jobServiceRestClient
                    .post()
                    .uri(builder -> builder
                            .path("/api/candidates/" + candidateId + "/jobs")
                            .queryParam("userId", userId)
                            .build()
                    )
                    .header("Authorization", makeBasicToken(username, password))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(jobDto)
                    .retrieve()
                    .toEntity(JobDTO.class);
            return createJobResponse.getBody() == null ? Optional.empty() : Optional.of(createJobResponse.getBody());
        } catch (RestClientResponseException e) {
            log.error("POST /api/candidates/" + candidateId + "/jobs" + " createJob userId={} httpStatus={}", userId, e.getStatusCode(), e);
            return Optional.empty();
        }
    }

    public Optional<JobDTO> updateJob(String id, String userId, String candidateId, JobUpdateDTO jobUpdateDTO) {
        try {
            var updateJobResponse = jobServiceRestClient
                    .put()
                    .uri(builder -> builder
                            .path("/api/candidates/" + candidateId + "/jobs/" + id)
                            .queryParam("userId", userId)
                            .build()
                    )
                    .header("Authorization", makeBasicToken(username, password))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(jobUpdateDTO)
                    .retrieve()
                    .toEntity(JobDTO.class);
            return updateJobResponse.getBody() == null ? Optional.empty() : Optional.of(updateJobResponse.getBody());
        } catch (RestClientResponseException e) {
            log.error("PUT /api/candidates/" + candidateId + "/jobs/" + id + " updateJob userId={} httpStatus={}", userId, e.getStatusCode(), e);
            throw new APIUpdateException(HttpStatus.resolve(e.getStatusCode().value()));
        }
    }

    public boolean deleteJob(String id, String userId, String candidateId) {
        try {
            jobServiceRestClient
                    .delete()
                    .uri(builder -> builder
                            .path("/api/candidates/" + candidateId + "/jobs/" + id)
                            .queryParam("userId", userId)
                            .build()
                    )
                    .header("Authorization", makeBasicToken(username, password))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            log.error("DELETE " + "/api/candidates/" + candidateId + "/jobs/" + id + " deleteJob userId={}", userId, e);
            return false;
        }


    }

    //for the main page
    public List<JobDTO> findAllJobsByCandidateId(String candidateId, String userId) {
        return findAllJobsForUserAndCandidateId(userId, candidateId)
                .stream().sorted((j1, j2) -> {
                    if (j1.endDate() == null && j2.endDate() == null) {
                        return 0;
                    }
                    if (j1.endDate() == null) {
                        return -1;
                    }
                    if (j2.endDate() == null) {
                        return 1;
                    }
                    return j2.endDate().compareTo(j1.endDate());
                }).toList();
    }

    public void deleteJobsByCandidateIdAndUserId(String candidateId, String userId) {
        try {
            jobServiceRestClient.delete()
                    .uri(uriBuilder -> uriBuilder.path("/api/candidates/" + candidateId + "/jobs")
                            .queryParam("userId", userId)
                            .build())
                    .header("Authorization", makeBasicToken(username, password))
                    .retrieve()
                    .toBodilessEntity();
        }catch (Exception e) {
            log.error("DELETE /api/candidates/all/jobs deleteJobsByUserId userId={}", userId, e);
        }
    }

    private String makeBasicToken(String username, String password) {
        var toBase64 = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(toBase64.getBytes(StandardCharsets.UTF_8));
    }
}
