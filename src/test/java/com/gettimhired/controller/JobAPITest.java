package com.gettimhired.controller;

import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.JobDTO;
import com.gettimhired.model.dto.update.JobUpdateDTO;
import com.gettimhired.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static com.gettimhired.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobAPITest {

    private JobAPI jobApi;
    private JobService jobService;
    private UserDetails userDetails;

    @BeforeEach
    public void init() {
        userDetails = mock(UserDetails.class);
        jobService = mock(JobService.class);
        jobApi = new JobAPI(jobService);
    }

    @Test
    public void testGetAllJobsHappy() {
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(jobService
                .findAllJobsForUserAndCandidateId(
                        USER_ID,
                        CANDIDATE_ID))
                .thenReturn(new ArrayList<>());

        var result = jobApi.getAllJobs(userDetails, CANDIDATE_ID);

        verify(userDetails, times(2)).getUsername();
        verify(jobService, times(1))
                .findAllJobsForUserAndCandidateId(
                        USER_ID,
                        CANDIDATE_ID
                );
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetJobByIdHappy() {
        var jobDto = getJobDto();
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(jobService.findJobByIdAndUserId(ID, USER_ID, CANDIDATE_ID)).thenReturn(Optional.of(jobDto));

        var result = jobApi.getJobById(userDetails, ID, CANDIDATE_ID);

        verify(userDetails, times(2)).getUsername();
        verify(jobService, times(1)).findJobByIdAndUserId(ID, USER_ID, CANDIDATE_ID);
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
        assertEquals(jobDto, result.getBody());
    }

    @Test
    public void testGetJobByIdNOtFound() {
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(jobService.findJobByIdAndUserId(ID, USER_ID, CANDIDATE_ID)).thenReturn(Optional.empty());

        var result = jobApi.getJobById(userDetails, ID, CANDIDATE_ID);

        verify(userDetails, times(2)).getUsername();
        verify(jobService, times(1)).findJobByIdAndUserId(ID, USER_ID, CANDIDATE_ID);
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(404), result.getStatusCode());
    }

    @Test
    public void testCreateJobHappy() {
        var jobDto = getJobDto();
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(jobService.createJob(USER_ID, CANDIDATE_ID, jobDto)).thenReturn(Optional.of(jobDto));

        var result = jobApi.createJob(userDetails, jobDto, CANDIDATE_ID);

        verify(userDetails, times(2)).getUsername();
        verify(jobService, times(1)).createJob(USER_ID, CANDIDATE_ID, jobDto);
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
        assertEquals(jobDto, result.getBody());
    }

    @Test
    public void testCreateJobFailed() {
        var jobDto = getJobDto();
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(jobService.createJob(USER_ID, CANDIDATE_ID, jobDto)).thenReturn(Optional.empty());

        var result = jobApi.createJob(userDetails, jobDto, CANDIDATE_ID);

        verify(userDetails, times(2)).getUsername();
        verify(jobService, times(1)).createJob(USER_ID, CANDIDATE_ID, jobDto);
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(500), result.getStatusCode());
    }

    @Test
    public void testUpdateJob_Success() {

        var jobUpdateDTO = getJobUpdateDto();
        var updatedJobDTO = getJobDto();
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(jobService.updateJob(any(String.class), any(String.class), any(String.class), any(JobUpdateDTO.class)))
                .thenReturn(Optional.of(updatedJobDTO));


        var response = jobApi.updateJob(userDetails, jobUpdateDTO, ID, CANDIDATE_ID);


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedJobDTO, response.getBody());
    }

    @Test
    public void testUpdateJob_Failure() {

        var jobUpdateDTO = getJobUpdateDto();
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(jobService.updateJob(any(String.class), any(String.class), any(String.class), any(JobUpdateDTO.class)))
                .thenReturn(Optional.empty());


        var response = jobApi.updateJob(userDetails, jobUpdateDTO, ID, CANDIDATE_ID);


        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testUpdateJob_APIUpdateException() {

        var jobUpdateDTO = getJobUpdateDto();
        APIUpdateException apiUpdateException = new APIUpdateException(HttpStatus.BAD_REQUEST);
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(jobService.updateJob(any(String.class), any(String.class), any(String.class), any(JobUpdateDTO.class)))
                .thenThrow(apiUpdateException);


        var response = jobApi.updateJob(userDetails, jobUpdateDTO, ID, CANDIDATE_ID);


        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeleteJob_Success() {

        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(jobService.deleteJob(anyString(), anyString(), anyString()))
                .thenReturn(true);


        var response = jobApi.deleteJob(userDetails, ID, CANDIDATE_ID);


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeleteJob_Failure() {

        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(jobService.deleteJob(anyString(), anyString(), anyString()))
                .thenReturn(false);


        var response = jobApi.deleteJob(userDetails, ID, CANDIDATE_ID);


        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    private JobUpdateDTO getJobUpdateDto() {
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

    private JobDTO getJobDto() {
        return new JobDTO(
                UUID.randomUUID().toString(),
                USER_ID,
                CANDIDATE_ID,
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