package com.gettimhired.controller;

import com.gettimhired.TestHelper;
import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.CandidateDTO;
import com.gettimhired.model.dto.update.CandidateUpdateDTO;
import com.gettimhired.service.CandidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CandidateAPITest {

    private CandidateAPI candidateAPI;
    private CandidateService candidateService;
    private UserDetails userDetails;

    @BeforeEach
    public void init() {
        candidateService = mock(CandidateService.class);
        userDetails = mock(UserDetails.class);
        candidateAPI = new CandidateAPI(candidateService);
    }

    @Test
    public void testGetAllCandidates() {
        when(userDetails.getUsername()).thenReturn("BARK");
        when(candidateService.findAllCandidatesForUser("BARK")).thenReturn(new ArrayList<>());

        var allCandidates = candidateAPI.getAllCandidates(userDetails);

        verify(userDetails, times(2)).getUsername();
        verify(candidateService, times(1)).findAllCandidatesForUser("BARK");
        assertEquals(0, allCandidates.size());
    }

    @Test
    public void testGetCandidateByUserIdAndIdHappy() {
        var candidateDto = new CandidateDTO(TestHelper.ID, TestHelper.USER_ID, "BARK_FNAME", "BARK_LNAME", "BARK_SUMMARY", "LinkedIn", "Github");
        when(candidateService.findCandidateByUserIdAndId(TestHelper.USER_ID, TestHelper.ID)).thenReturn(Optional.of(candidateDto));
        when(userDetails.getUsername()).thenReturn(TestHelper.USER_ID);

        var response = candidateAPI.getCandidateById(userDetails, TestHelper.ID);

        verify(candidateService, times(1)).findCandidateByUserIdAndId(TestHelper.USER_ID, TestHelper.ID);
        verify(userDetails, times(2)).getUsername();
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200) ,response.getStatusCode());
        assertEquals(candidateDto, response.getBody());
    }

    @Test
    public void testGetCandidateByUserIdAndId404() {
        when(candidateService.findCandidateByUserIdAndId(TestHelper.USER_ID, TestHelper.ID)).thenReturn(Optional.empty());
        when(userDetails.getUsername()).thenReturn(TestHelper.USER_ID);

        var response = candidateAPI.getCandidateById(userDetails, TestHelper.ID);

        verify(candidateService, times(1)).findCandidateByUserIdAndId(TestHelper.USER_ID, TestHelper.ID);
        verify(userDetails, times(2)).getUsername();
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(404) ,response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testCreateCandidateHappy() {

        var candidateDtoIn = new CandidateDTO(null, null, "Bark", "McBarkson", "Summary Bark", "LinkedIn", "Github");
        var candidateDtoOutOpt = Optional.of(
                new CandidateDTO(
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        "Bark",
                        "McBarkson",
                        "Summary Bark",
                        "LinkedIn",
                        "Github"
                )
        );
        when(candidateService.createCandidate(TestHelper.USER_ID, candidateDtoIn)).thenReturn(candidateDtoOutOpt);
        when(userDetails.getUsername()).thenReturn(TestHelper.USER_ID);

        var result = candidateAPI.createCandidate(userDetails, candidateDtoIn);

        verify(candidateService, times(1)).createCandidate(TestHelper.USER_ID, candidateDtoIn);
        verify(userDetails, times(2)).getUsername();
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
    }

    @Test
    public void testCreateCandidateFailedToSave() {

        var candidateDtoIn = new CandidateDTO(null, null, "Bark", "McBarkson", "Summary Bark", "LinkedIn", "Github");
        when(candidateService.createCandidate(TestHelper.USER_ID, candidateDtoIn)).thenReturn(Optional.empty());
        when(userDetails.getUsername()).thenReturn(TestHelper.USER_ID);

        var result = candidateAPI.createCandidate(userDetails, candidateDtoIn);

        verify(candidateService, times(1)).createCandidate(TestHelper.USER_ID, candidateDtoIn);
        verify(userDetails, times(2)).getUsername();
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(500), result.getStatusCode());
    }

    @Test
    public void testUpdateCandidateHappy() {
        var candidateUpdateDto = new CandidateUpdateDTO(
                "BARK_FNAME_UPDATE",
                "BARK_LNAME_UPDATE",
                "BARK_SUMMARY_UPDATE",
                "LinkedIn",
                "Github"
        );
        CandidateDTO candidateDto = new CandidateDTO(
                TestHelper.ID,
                TestHelper.USER_ID,
                "BARK_FNAME",
                "BARK_LNAME",
                "BARK_SUMMARY",
                "LinkedIn",
                "Github"
        );
        when(candidateService.updateCandidate(TestHelper.ID, TestHelper.USER_ID, candidateUpdateDto)).thenReturn(Optional.of(candidateDto));
        when(userDetails.getUsername()).thenReturn(TestHelper.USER_ID);

        var result = candidateAPI.updateCandidate(userDetails, candidateUpdateDto, TestHelper.ID);

        verify(candidateService, times(1)).updateCandidate(TestHelper.ID, TestHelper.USER_ID, candidateUpdateDto);
        verify(userDetails, times(2)).getUsername();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    public void testUpdateCandidateReturnsEmpty() {
        var candidateUpdateDto = new CandidateUpdateDTO(
                "BARK_FNAME_UPDATE",
                "BARK_LNAME_UPDATE",
                "BARK_SUMMARY_UPDATE",
                "LinkedIn",
                "Github"
        );
        when(candidateService.updateCandidate(TestHelper.ID, TestHelper.USER_ID, candidateUpdateDto)).thenReturn(Optional.empty());
        when(userDetails.getUsername()).thenReturn(TestHelper.USER_ID);

        var result = candidateAPI.updateCandidate(userDetails, candidateUpdateDto, TestHelper.ID);

        verify(candidateService, times(1)).updateCandidate(TestHelper.ID, TestHelper.USER_ID, candidateUpdateDto);
        verify(userDetails, times(2)).getUsername();
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    public void testUpdateCandidateThrowsCandidateUpdateException() {
        var candidateUpdateDto = new CandidateUpdateDTO(
                "BARK_FNAME_UPDATE",
                "BARK_LNAME_UPDATE",
                "BARK_SUMMARY_UPDATE",
                "LinkedIn",
                "Github"
        );
        when(candidateService.updateCandidate(TestHelper.ID, TestHelper.USER_ID, candidateUpdateDto)).thenThrow(new APIUpdateException(HttpStatus.NOT_FOUND));
        when(userDetails.getUsername()).thenReturn(TestHelper.USER_ID);

        var result = candidateAPI.updateCandidate(userDetails, candidateUpdateDto, TestHelper.ID);

        verify(candidateService, times(1)).updateCandidate(TestHelper.ID, TestHelper.USER_ID, candidateUpdateDto);
        verify(userDetails, times(2)).getUsername();
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    public void testDeleteCandidateHappy() {
        when(userDetails.getUsername()).thenReturn(TestHelper.USER_ID);
        when(candidateService.deleteCandidate(TestHelper.ID, TestHelper.USER_ID)).thenReturn(true);

        var result = candidateAPI.deleteCandidate(userDetails, TestHelper.ID);

        verify(userDetails, times(2)).getUsername();
        verify(candidateService, times(1)).deleteCandidate(TestHelper.ID, TestHelper.USER_ID);
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
    }

    @Test
    public void testDeleteCandidateServiceReturnsFalse() {
        when(userDetails.getUsername()).thenReturn(TestHelper.USER_ID);
        when(candidateService.deleteCandidate(TestHelper.ID, TestHelper.USER_ID)).thenReturn(false);

        var result = candidateAPI.deleteCandidate(userDetails, TestHelper.ID);

        verify(userDetails, times(2)).getUsername();
        verify(candidateService, times(1)).deleteCandidate(TestHelper.ID, TestHelper.USER_ID);
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(500), result.getStatusCode());
    }
}