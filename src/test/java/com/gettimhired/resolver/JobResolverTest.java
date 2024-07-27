package com.gettimhired.resolver;

import com.gettimhired.model.dto.JobDTO;
import com.gettimhired.model.dto.input.JobInputDTO;
import com.gettimhired.model.dto.update.JobUpdateDTO;
import com.gettimhired.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JobResolverTest {

    private JobService jobService;
    private UserDetails userDetails;
    private JobResolver jobResolver;

    @BeforeEach
    void setUp() {
        jobService = mock(JobService.class);
        userDetails = mock(UserDetails.class);
        jobResolver = new JobResolver(jobService);
    }

    @Test
    void testGetJobs() {
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.findAllJobsForUserAndCandidateId("user1", "userId1")).thenReturn(Collections.emptyList());

        List<JobDTO> result = jobResolver.getJobs(userDetails, "userId1");

        assertEquals(Collections.emptyList(), result);

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> candidateIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(jobService, times(1)).findAllJobsForUserAndCandidateId(userIdCaptor.capture(), candidateIdCaptor.capture());
        assertEquals("user1", userIdCaptor.getValue());
        assertEquals("userId1", candidateIdCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

    @Test
    void testGetJobById() {
        JobDTO job = new JobDTO("1", "user1", "userId1", "candidateId1", "Company", LocalDate.parse("2016-01-01"), LocalDate.parse("2017-01-01"), Collections.emptyList(), Collections.emptyList(), true, "BARK_LEAVE");
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.findJobByIdAndUserId(eq("1"), eq("user1"), eq("candidateId"))).thenReturn(Optional.of(job));

        JobDTO result = jobResolver.getJobById(userDetails, "1", "candidateId");

        assertEquals(job, result);

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(jobService, times(1)).findJobByIdAndUserId(idCaptor.capture(), userIdCaptor.capture(), eq("candidateId"));
        assertEquals("1", idCaptor.getValue());
        assertEquals("user1", userIdCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

    @Test
    void testCreateJob() {
        JobInputDTO jobInputDTO = new JobInputDTO("1", "userId1", "candidateId1", "Company", "title", LocalDate.parse("2016-01-01"), LocalDate.parse("2017-01-01"), Collections.emptyList(), Collections.emptyList(), true, "BARK_LEAVE");
        JobDTO jobDTO = new JobDTO(jobInputDTO);
        when(userDetails.getUsername()).thenReturn("userId1");
        when(jobService.createJob("userId1", "candidateId1", jobDTO)).thenReturn(Optional.of(jobDTO));

        JobDTO result = jobResolver.createJob(userDetails, jobInputDTO);

        assertEquals(jobDTO, result);

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> candidateIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<JobDTO> jobCaptor = ArgumentCaptor.forClass(JobDTO.class);
        verify(jobService, times(1)).createJob(userIdCaptor.capture(), candidateIdCaptor.capture(), jobCaptor.capture());
        assertEquals("userId1", userIdCaptor.getValue());
        assertEquals("candidateId1", candidateIdCaptor.getValue());
        assertEquals(jobDTO, jobCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

    @Test
    void testUpdateJob() {
        JobInputDTO jobInputDTO = new JobInputDTO("1", "userId1", "candidateId1", "Company", "title", LocalDate.parse("2016-01-01"), LocalDate.parse("2017-01-01"), Collections.emptyList(), Collections.emptyList(), true, "BARK_LEAVE");
        JobUpdateDTO jobUpdateDTO = new JobUpdateDTO(jobInputDTO);
        JobDTO jobDTO = new JobDTO(jobInputDTO);
        when(userDetails.getUsername()).thenReturn("userId1");
        when(jobService.updateJob("1", "userId1", "candidateId1", jobUpdateDTO)).thenReturn(Optional.of(jobDTO));

        JobDTO result = jobResolver.updateJob(userDetails, jobInputDTO);

        assertEquals(jobDTO, result);

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> candidateIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<JobUpdateDTO> updateCaptor = ArgumentCaptor.forClass(JobUpdateDTO.class);
        verify(jobService, times(1)).updateJob(idCaptor.capture(), userIdCaptor.capture(), candidateIdCaptor.capture(), updateCaptor.capture());
        assertEquals("1", idCaptor.getValue());
        assertEquals("userId1", userIdCaptor.getValue());
        assertEquals("candidateId1", candidateIdCaptor.getValue());
        assertEquals(jobUpdateDTO, updateCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

    @Test
    void testDeleteJob() {
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.deleteJob(eq("1"), eq("user1"), eq("candidateId"))).thenReturn(true);

        boolean result = jobResolver.deleteJob(userDetails, "1", "candidateId");

        assertEquals(true, result);

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(jobService, times(1)).deleteJob(idCaptor.capture(), userIdCaptor.capture(), eq("candidateId"));
        assertEquals("1", idCaptor.getValue());
        assertEquals("user1", userIdCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

}