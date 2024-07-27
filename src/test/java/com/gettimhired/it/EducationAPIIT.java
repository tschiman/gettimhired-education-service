package com.gettimhired.it;

import com.gettimhired.config.TestSecurityConfig;
import com.gettimhired.controller.EducationAPI;
import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.EducationDTO;
import com.gettimhired.model.dto.update.EducationUpdateDTO;
import com.gettimhired.service.EducationService;
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

@WebMvcTest(EducationAPI.class)
@Import(TestSecurityConfig.class)
class EducationAPIIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EducationService educationService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetAllEducations() throws Exception {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.findAllEducationsForUserAndCandidateId("user1", candidateId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/candidates/{candidateId}/educations", candidateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(educationService, times(1)).findAllEducationsForUserAndCandidateId("user1", candidateId);
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetEducationById_Found() throws Exception {
        String candidateId = "1";
        String educationId = "1";
        EducationDTO educationDTO = new EducationDTO(null,null,null,null,null,null,null,null,null);
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.findEducationByIdAndUserId(educationId, "user1")).thenReturn(Optional.of(educationDTO));

        mockMvc.perform(get("/api/candidates/{candidateId}/educations/{id}", candidateId, educationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(educationService, times(1)).findEducationByIdAndUserId(educationId, "user1");
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetEducationById_NotFound() throws Exception {
        String candidateId = "1";
        String educationId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.findEducationByIdAndUserId(educationId, "user1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/candidates/{candidateId}/educations/{id}", candidateId, educationId))
                .andExpect(status().isNotFound());

        verify(educationService, times(1)).findEducationByIdAndUserId(educationId, "user1");
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreateEducation_Success() throws Exception {
        String candidateId = "1";
        EducationDTO educationDTO = new EducationDTO(null,null,null,null,null,null,null,null,null);
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.createEducation(eq("user1"), eq(candidateId), any(EducationDTO.class))).thenReturn(Optional.of(educationDTO));

        mockMvc.perform(post("/api/candidates/{candidateId}/educations", candidateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"Bark School\",\n" +
                                "    \"startDate\": \"2021-01-01\",\n" +
                                "    \"endDate\": \"2022-01-01\",\n" +
                                "    \"graduated\": true,\n" +
                                "    \"areaOfStudy\": \"Barking\",\n" +
                                "    \"educationLevel\": \"BACHELORS\"\n" +
                                "}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(educationService, times(1)).createEducation(eq("user1"), eq(candidateId), any(EducationDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreateEducation_Failure() throws Exception {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.createEducation(eq("user1"), eq(candidateId), any(EducationDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/candidates/{candidateId}/educations", candidateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"Bark School\",\n" +
                                "    \"startDate\": \"2021-01-01\",\n" +
                                "    \"endDate\": \"2022-01-01\",\n" +
                                "    \"graduated\": true,\n" +
                                "    \"areaOfStudy\": \"Barking\",\n" +
                                "    \"educationLevel\": \"BACHELORS\"\n" +
                                "}"))
                .andExpect(status().isInternalServerError());

        verify(educationService, times(1)).createEducation(eq("user1"), eq(candidateId), any(EducationDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateEducation_Success() throws Exception {
        String candidateId = "1";
        String educationId = "1";
        EducationDTO educationDTO = new EducationDTO(null,null,null,null,null,null,null,null,null);
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.updateEducation(eq(educationId), eq("user1"), eq(candidateId), any(EducationUpdateDTO.class))).thenReturn(Optional.of(educationDTO));

        mockMvc.perform(put("/api/candidates/{candidateId}/educations/{id}", candidateId, educationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"Updated Bark School\",\n" +
                                "    \"startDate\": \"2021-01-01\",\n" +
                                "    \"endDate\": \"2022-01-01\",\n" +
                                "    \"graduated\": true,\n" +
                                "    \"areaOfStudy\": \"Advanced Barking\",\n" +
                                "    \"educationLevel\": \"NONE\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(educationService, times(1)).updateEducation(eq(educationId), eq("user1"), eq(candidateId), any(EducationUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateEducation_Failure() throws Exception {
        String candidateId = "1";
        String educationId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.updateEducation(eq(educationId), eq("user1"), eq(candidateId), any(EducationUpdateDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/candidates/{candidateId}/educations/{id}", candidateId, educationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"Updated Bark School\",\n" +
                                "    \"startDate\": \"2021-01-01\",\n" +
                                "    \"endDate\": \"2022-01-01\",\n" +
                                "    \"graduated\": true,\n" +
                                "    \"areaOfStudy\": \"Advanced Barking\",\n" +
                                "    \"educationLevel\": \"MASTERS\"\n" +
                                "}"))
                .andExpect(status().isInternalServerError());

        verify(educationService, times(1)).updateEducation(eq(educationId), eq("user1"), eq(candidateId), any(EducationUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateEducation_APIUpdateException() throws Exception {
        String candidateId = "1";
        String educationId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.updateEducation(eq(educationId), eq("user1"), eq(candidateId), any(EducationUpdateDTO.class))).thenThrow(new APIUpdateException(HttpStatus.FORBIDDEN));

        mockMvc.perform(put("/api/candidates/{candidateId}/educations/{id}", candidateId, educationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"Updated Bark School\",\n" +
                                "    \"startDate\": \"2021-01-01\",\n" +
                                "    \"endDate\": \"2022-01-01\",\n" +
                                "    \"graduated\": true,\n" +
                                "    \"areaOfStudy\": \"Advanced Barking\",\n" +
                                "    \"educationLevel\": \"MASTERS\"\n" +
                                "}"))
                .andExpect(status().isForbidden());

        verify(educationService, times(1)).updateEducation(eq(educationId), eq("user1"), eq(candidateId), any(EducationUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteEducation_Success() throws Exception {
        String candidateId = "1";
        String educationId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.deleteEducation(eq(educationId), eq("user1"))).thenReturn(true);

        mockMvc.perform(delete("/api/candidates/{candidateId}/educations/{id}", candidateId, educationId))
                .andExpect(status().isOk());

        verify(educationService, times(1)).deleteEducation(eq(educationId), eq("user1"));
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteEducation_Failure() throws Exception {
        String candidateId = "1";
        String educationId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.deleteEducation(eq(educationId), eq("user1"))).thenReturn(false);

        mockMvc.perform(delete("/api/candidates/{candidateId}/educations/{id}", candidateId, educationId))
                .andExpect(status().isInternalServerError());

        verify(educationService, times(1)).deleteEducation(eq(educationId), eq("user1"));
    }
}