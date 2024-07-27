package com.gettimhired.it;

import com.gettimhired.config.TestSecurityConfig;
import com.gettimhired.controller.JobAPI;
import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.JobDTO;
import com.gettimhired.model.dto.update.JobUpdateDTO;
import com.gettimhired.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobAPI.class)
@Import(TestSecurityConfig.class)
class JobAPIIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetAllJobs() throws Exception {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.findAllJobsForUserAndCandidateId("user1", candidateId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/candidates/{candidateId}/jobs", candidateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(jobService, times(1)).findAllJobsForUserAndCandidateId("user1", candidateId);
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetJobById_Found() throws Exception {
        String candidateId = "1";
        String jobId = "1";
        JobDTO jobDTO = new JobDTO(null,null,null,null,null,null,null,null,null,null,null);
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.findJobByIdAndUserId(jobId, "user1", candidateId)).thenReturn(Optional.of(jobDTO));

        mockMvc.perform(get("/api/candidates/{candidateId}/jobs/{id}", candidateId, jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(jobService, times(1)).findJobByIdAndUserId(jobId, "user1", candidateId);
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetJobById_NotFound() throws Exception {
        String candidateId = "1";
        String jobId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.findJobByIdAndUserId(jobId, "user1", candidateId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/candidates/{candidateId}/jobs/{id}", candidateId, jobId))
                .andExpect(status().isNotFound());

        verify(jobService, times(1)).findJobByIdAndUserId(jobId, "user1", candidateId);
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreateJob_Success() throws Exception {
        String candidateId = "1";
        JobDTO jobDTO = new JobDTO(null,null,null,null,null,null,null,null,null,null,null);
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.createJob(eq("user1"), eq(candidateId), any(JobDTO.class))).thenReturn(Optional.of(jobDTO));

        mockMvc.perform(post("/api/candidates/{candidateId}/jobs", candidateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"companyName\": \"Bark\",\n" +
                                "    \"title\": \"Bark\",\n" +
                                "    \"startDate\": \"2023-08-01\",\n" +
                                "    \"endDate\": \"2023-09-01\",\n" +
                                "    \"skills\": [\n" +
                                "        \"Bark\"\n" +
                                "    ],\n" +
                                "    \"achievements\": [\n" +
                                "        \"Bark\"\n" +
                                "    ],\n" +
                                "    \"currentlyWorking\": true,\n" +
                                "    \"reasonForLeaving\": \"Test\"\n" +
                                "}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(jobService, times(1)).createJob(eq("user1"), eq(candidateId), any(JobDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreateJob_Failure() throws Exception {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.createJob(eq("user1"), eq(candidateId), any(JobDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/candidates/{candidateId}/jobs", candidateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"companyName\": \"Bark\",\n" +
                                "    \"title\": \"Bark\",\n" +
                                "    \"startDate\": \"2023-08-01\",\n" +
                                "    \"endDate\": \"2023-09-01\",\n" +
                                "    \"skills\": [\n" +
                                "        \"Bark\"\n" +
                                "    ],\n" +
                                "    \"achievements\": [\n" +
                                "        \"Bark\"\n" +
                                "    ],\n" +
                                "    \"currentlyWorking\": true,\n" +
                                "    \"reasonForLeaving\": \"Test\"\n" +
                                "}"))
                .andExpect(status().isInternalServerError());

        verify(jobService, times(1)).createJob(eq("user1"), eq(candidateId), any(JobDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateJob_Success() throws Exception {
        String candidateId = "1";
        String jobId = "1";
        JobDTO jobDTO = new JobDTO(null,null,null,null,null,null,null,null,null,null,null);
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.updateJob(eq(jobId), eq("user1"), eq(candidateId), any(JobUpdateDTO.class))).thenReturn(Optional.of(jobDTO));

        mockMvc.perform(put("/api/candidates/{candidateId}/jobs/{id}", candidateId, jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"companyName\": \"Bark\",\n" +
                                "    \"title\": \"Bark\",\n" +
                                "    \"startDate\": \"2023-08-01\",\n" +
                                "    \"endDate\": \"2023-09-01\",\n" +
                                "    \"skills\": [\n" +
                                "        \"Bark\"\n" +
                                "    ],\n" +
                                "    \"achievements\": [\n" +
                                "        \"Bark\"\n" +
                                "    ],\n" +
                                "    \"currentlyWorking\": true,\n" +
                                "    \"reasonForLeaving\": \"Test\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(jobService, times(1)).updateJob(eq(jobId), eq("user1"), eq(candidateId), any(JobUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateJob_Failure() throws Exception {
        String candidateId = "1";
        String jobId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.updateJob(eq(jobId), eq("user1"), eq(candidateId), any(JobUpdateDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/candidates/{candidateId}/jobs/{id}", candidateId, jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"companyName\": \"Bark\",\n" +
                                "    \"title\": \"Bark\",\n" +
                                "    \"startDate\": \"2023-08-01\",\n" +
                                "    \"endDate\": \"2023-09-01\",\n" +
                                "    \"skills\": [\n" +
                                "        \"Bark\"\n" +
                                "    ],\n" +
                                "    \"achievements\": [\n" +
                                "        \"Bark\"\n" +
                                "    ],\n" +
                                "    \"currentlyWorking\": true,\n" +
                                "    \"reasonForLeaving\": \"Test\"\n" +
                                "}"))
                .andExpect(status().isInternalServerError());

        verify(jobService, times(1)).updateJob(eq(jobId), eq("user1"), eq(candidateId), any(JobUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateJob_APIUpdateException() throws Exception {
        String candidateId = "1";
        String jobId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.updateJob(eq(jobId), eq("user1"), eq(candidateId), any(JobUpdateDTO.class))).thenThrow(new APIUpdateException(HttpStatus.FORBIDDEN));

        mockMvc.perform(put("/api/candidates/{candidateId}/jobs/{id}", candidateId, jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"companyName\": \"Bark\",\n" +
                                "    \"title\": \"Bark\",\n" +
                                "    \"startDate\": \"2023-08-01\",\n" +
                                "    \"endDate\": \"2023-09-01\",\n" +
                                "    \"skills\": [\n" +
                                "        \"Bark\"\n" +
                                "    ],\n" +
                                "    \"achievements\": [\n" +
                                "        \"Bark\"\n" +
                                "    ],\n" +
                                "    \"currentlyWorking\": true,\n" +
                                "    \"reasonForLeaving\": \"Test\"\n" +
                                "}"))
                .andExpect(status().isForbidden());

        verify(jobService, times(1)).updateJob(eq(jobId), eq("user1"), eq(candidateId), any(JobUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteJob_Success() throws Exception {
        String candidateId = "1";
        String jobId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.deleteJob(eq(jobId), eq("user1"), eq(candidateId))).thenReturn(true);

        mockMvc.perform(delete("/api/candidates/{candidateId}/jobs/{id}", candidateId, jobId))
                .andExpect(status().isOk());

        verify(jobService, times(1)).deleteJob(eq(jobId), eq("user1"), eq(candidateId));
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteJob_Failure() throws Exception {
        String candidateId = "1";
        String jobId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.deleteJob(eq(jobId), eq("user1"), eq(candidateId))).thenReturn(false);

        mockMvc.perform(delete("/api/candidates/{candidateId}/jobs/{id}", candidateId, jobId))
                .andExpect(status().isInternalServerError());

        verify(jobService, times(1)).deleteJob(eq(jobId), eq("user1"), eq(candidateId));
    }
}