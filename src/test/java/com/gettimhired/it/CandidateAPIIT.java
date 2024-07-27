package com.gettimhired.it;

import com.gettimhired.TestHelper;
import com.gettimhired.config.TestSecurityConfig;
import com.gettimhired.controller.CandidateAPI;
import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.CandidateDTO;
import com.gettimhired.model.dto.update.CandidateUpdateDTO;
import com.gettimhired.service.CandidateService;
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

@WebMvcTest(CandidateAPI.class)
@Import(TestSecurityConfig.class)
class CandidateAPIIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CandidateService candidateService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetAllCandidates() throws Exception {
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.findAllCandidatesForUser("user1")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(candidateService, times(1)).findAllCandidatesForUser("user1");
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetCandidateById_Found() throws Exception {
        String candidateId = "1";
        CandidateDTO candidate = new CandidateDTO(candidateId, "user1", null, null, null, "LinkedIn", "Github");
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.findCandidateByUserIdAndId("user1", candidateId)).thenReturn(Optional.of(candidate));

        mockMvc.perform(get("/api/candidates/{id}", candidateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(candidateService, times(1)).findCandidateByUserIdAndId("user1", candidateId);
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetCandidateById_NotFound() throws Exception {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.findCandidateByUserIdAndId("user1", candidateId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/candidates/{id}", candidateId))
                .andExpect(status().isNotFound());

        verify(candidateService, times(1)).findCandidateByUserIdAndId("user1", candidateId);
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreateCandidate_Success() throws Exception {
        CandidateDTO candidateDTO = new CandidateDTO(TestHelper.ID,TestHelper.USER_ID,"Bark","Bark","Bark", "LinkedIn", "Github");
        when(userDetails.getUsername()).thenReturn(TestHelper.USER_ID);
        when(candidateService.createCandidate(eq("user1"), any(CandidateDTO.class))).thenReturn(Optional.of(candidateDTO));

        mockMvc.perform(post("/api/candidates")
                        
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"firstName\": \"Bark\",\n" +
                                "    \"lastName\": \"Bark\",\n" +
                                "    \"summary\": \"Bark\"\n" +
                                "}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(candidateService, times(1)).createCandidate(eq("user1"), any(CandidateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreateCandidate_Failure() throws Exception {
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.createCandidate(eq("user1"), any(CandidateDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/candidates")
                        
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"firstName\": \"Bark\",\n" +
                                "    \"lastName\": \"Bark\",\n" +
                                "    \"summary\": \"Bark\"\n" +
                                "}"))
                .andExpect(status().isInternalServerError());

        verify(candidateService, times(1)).createCandidate(eq("user1"), any(CandidateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateCandidate_Success() throws Exception {
        String candidateId = "1";
        CandidateDTO candidateDTO = new CandidateDTO("1","user1",null,null,null, "LinkedIn", "Github");
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.updateCandidate(eq(candidateId), eq("user1"), any(CandidateUpdateDTO.class))).thenReturn(Optional.of(candidateDTO));

        mockMvc.perform(put("/api/candidates/{id}", candidateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"firstName\": \"Bark\",\n" +
                                "    \"lastName\": \"Bark\",\n" +
                                "    \"summary\": \"BARK\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(candidateService, times(1)).updateCandidate(eq(candidateId), eq("user1"), any(CandidateUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateCandidate_Failure() throws Exception {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.updateCandidate(eq(candidateId), eq("user1"), any(CandidateUpdateDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/candidates/{id}", candidateId)
                        
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"firstName\": \"Bark\",\n" +
                                "    \"lastName\": \"Bark\",\n" +
                                "    \"summary\": \"BARK\"\n" +
                                "}"))
                .andExpect(status().isInternalServerError());

        verify(candidateService, times(1)).updateCandidate(eq(candidateId), eq("user1"), any(CandidateUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateCandidate_APIUpdateException() throws Exception {
        String candidateId = "1";
        CandidateUpdateDTO candidateUpdateDTO = new CandidateUpdateDTO(null,null,null, "LinkedIn", "Github");
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.updateCandidate(eq(candidateId), eq("user1"), any(CandidateUpdateDTO.class))).thenThrow(new APIUpdateException(HttpStatus.FORBIDDEN));

        mockMvc.perform(put("/api/candidates/{id}", candidateId)
                        
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"firstName\": \"Bark\",\n" +
                                "    \"lastName\": \"Bark\",\n" +
                                "    \"summary\": \"BARK\"\n" +
                                "}"))
                .andExpect(status().isForbidden());

        verify(candidateService, times(1)).updateCandidate(eq(candidateId), eq("user1"), any(CandidateUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteCandidate_Success() throws Exception {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.deleteCandidate(eq(candidateId), eq("user1"))).thenReturn(true);

        mockMvc.perform(delete("/api/candidates/{id}", candidateId))
                .andExpect(status().isOk());

        verify(candidateService, times(1)).deleteCandidate(candidateId, "user1");
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteCandidate_Failure() throws Exception {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.deleteCandidate(eq(candidateId), eq("user1"))).thenReturn(false);

        mockMvc.perform(delete("/api/candidates/{id}", candidateId))
                .andExpect(status().isInternalServerError());

        verify(candidateService, times(1)).deleteCandidate(candidateId, "user1");
    }
}