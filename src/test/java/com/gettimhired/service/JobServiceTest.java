package com.gettimhired.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gettimhired.TestHelper;
import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.JobDTO;
import com.gettimhired.model.dto.update.JobUpdateDTO;
import com.gettimhired.model.mongo.Job;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gettimhired.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

class JobServiceTest {

    private JobService jobService;
    private RestClient restClient;
    private MockWebServer mockWebServer;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        restClient = RestClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        jobService = new JobService(restClient, "username", "password");
    }

    @AfterEach
    public void shutDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testfindAllJobsForUserAndCandidateIdHappy() throws JsonProcessingException {
        var e1 = getJob("BARK_NAME");
        var e2 = getJob("BARK_NAME_TWO");
        var jobs = List.of(e1, e2);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(jobs))
                .setHeader("Content-Type", "application/json")
        );

        var result = jobService.findAllJobsForUserAndCandidateId(TestHelper.USER_ID, TestHelper.CANDIDATE_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testFindJobByUserIdAndCandidateIdAndId_Found() throws JsonProcessingException {

        var job = getJob("BARK_NAME");
        var expectedJobDTO = new JobDTO(job);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(job))
                .setHeader("Content-Type", "application/json")
        );

        Optional<JobDTO> result = jobService.findJobByIdAndUserId(ID, USER_ID, "candidateId");

        assertTrue(result.isPresent());
        assertEquals(expectedJobDTO, result.get());
    }

    @Test
    public void testFindJobByUserIdAndCandidateIdAndId_NotFound() {

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
        );

        Optional<JobDTO> result = jobService.findJobByIdAndUserId(ID, USER_ID, "candidateId");

        assertFalse(result.isPresent());
    }

    @Test
    public void testCreateJob_Success() throws JsonProcessingException {
        var job = getJob("BARK_NAME");;
        JobDTO jobDTO = new JobDTO(job);
        mockWebServer.enqueue(new MockResponse()
                        .setResponseCode(200)
                        .setBody(objectMapper.writeValueAsString(job))
                        .setHeader("Content-Type", "application/json")
        );

        Optional<JobDTO> result = jobService.createJob(TestHelper.USER_ID, TestHelper.CANDIDATE_ID, jobDTO);

        assertTrue(result.isPresent());
        assertEquals(new JobDTO(job), result.get());
    }

    @Test
    public void testCreateJob_Failure() {
        JobDTO jobDTO = new JobDTO(getJob("BARK_NAME"));
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
        );

        Optional<JobDTO> result = jobService.createJob(TestHelper.USER_ID, TestHelper.CANDIDATE_ID, jobDTO);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateJob_JobNotFound() {
        var jobUpdateDto = getJobUpdate();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
        );

        var ex = assertThrows(APIUpdateException.class, () -> jobService.updateJob(TestHelper.ID, TestHelper.USER_ID, TestHelper.CANDIDATE_ID, jobUpdateDto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    }

    @Test
    public void testUpdateJob_UserIdNotMatch() {
        var jobUpdateDto = getJobUpdate();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(403)
        );

        var ex = assertThrows(APIUpdateException.class, () -> jobService.updateJob(TestHelper.ID, "NO_MATCH", TestHelper.CANDIDATE_ID, jobUpdateDto));

        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
    }

    @Test
    public void testUpdateJob_CandidateIdNotMatch() {
        var jobUpdateDto = getJobUpdate();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(403)
        );

        var ex = assertThrows(APIUpdateException.class, () -> jobService.updateJob(TestHelper.ID, TestHelper.USER_ID, "NO_MATCH", jobUpdateDto));

        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
    }

    @Test
    public void testUpdateJob_SaveThrowsException() {
        var jobUpdateDto = getJobUpdate();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
        );

        var ex = assertThrows(APIUpdateException.class, () -> jobService.updateJob(TestHelper.ID, TestHelper.USER_ID, TestHelper.CANDIDATE_ID, jobUpdateDto));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getHttpStatus());
    }
    @Test
    public void testUpdateJob_Happy() throws JsonProcessingException {
        var job = getJob("BARK_NAME");
        var jobUpdateDto = getJobUpdate();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(job))
                .setHeader("Content-Type", "application/json")
        );

        var result = jobService.updateJob(TestHelper.ID, TestHelper.USER_ID, TestHelper.CANDIDATE_ID, jobUpdateDto);

        assertTrue(result.isPresent());
    }

    @Test
    public void testDeleteJob_Success() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        boolean result = jobService.deleteJob(TestHelper.ID, TestHelper.USER_ID, "candidateId");

        assertTrue(result);
    }

    @Test
    public void testDeleteJob_Failure() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        boolean result = jobService.deleteJob(TestHelper.ID, TestHelper.USER_ID, "candidateId");

        assertFalse(result);
    }

    @Test
    public void testFindAllJobsByCandidateId_Sorting() throws JsonProcessingException {

        var j1 = new JobDTO(null,null,null,null,null,null,LocalDate.of(2000,1,1),null,null,null,null);
        var j2 = new JobDTO(null,null,null,null,null,null,LocalDate.of(2020,1,1),null,null,null,null);
        var j3 = new JobDTO(null,null,null,null,null,null,null,null,null,null,null);
        var jobs = List.of(j1, j2, j3);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(jobs))
                .setHeader("Content-Type", "application/json")
        );

        var result = jobService.findAllJobsByCandidateId(CANDIDATE_ID, "userId");

        assertEquals(3, result.size());
        assertEquals(j3, result.get(0));
        assertEquals(j2, result.get(1));
        assertEquals(j1, result.get(2));
    }

    private static Job getJob(String name) {
        return new Job(
                UUID.randomUUID().toString(),
                USER_ID,
                CANDIDATE_ID,
                name,
                "BARK_TITLE",
                LocalDate.now(),
                LocalDate.now(),
                new ArrayList<>(),
                new ArrayList<>(),
                true,
                "BARK_LEAVE"
        );
    }

    private JobUpdateDTO getJobUpdate() {
        return new JobUpdateDTO(
                "BARK_NAME",
                "BARK_TITLE",
                LocalDate.now(),
                LocalDate.now(),
                new ArrayList<>(),
                new ArrayList<>(),
                true,
                "BARK_LEAVE"
        );
    }

}