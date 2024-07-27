package com.gettimhired.controller;

import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.EducationDTO;
import com.gettimhired.model.dto.update.EducationUpdateDTO;
import com.gettimhired.model.mongo.EducationLevel;
import com.gettimhired.service.EducationService;
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
import static org.mockito.Mockito.*;

class EducationAPITest {

    private EducationAPI educationAPI;
    private EducationService educationService;
    private UserDetails userDetails;

    @BeforeEach
    public void init() {
        userDetails = mock(UserDetails.class);
        educationService = mock(EducationService.class);
        educationAPI = new EducationAPI(educationService);
    }

    @Test
    public void testGetAllEducationsHappy() {
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(educationService
                .findAllEducationsForUserAndCandidateId(
                        USER_ID,
                        CANDIDATE_ID))
                .thenReturn(new ArrayList<>());

        var result = educationAPI.getAllEducations(userDetails, CANDIDATE_ID);

        verify(userDetails, times(2)).getUsername();
        verify(educationService, times(1))
                .findAllEducationsForUserAndCandidateId(
                    USER_ID,
                    CANDIDATE_ID
                );
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetEducationByIdHappy() {
        var educationDto = getEducationDto();
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(educationService.findEducationByIdAndUserId(ID, USER_ID)).thenReturn(Optional.of(educationDto));

        var result = educationAPI.getEducationById(userDetails, ID, CANDIDATE_ID);

        verify(userDetails, times(2)).getUsername();
        verify(educationService, times(1)).findEducationByIdAndUserId(ID, USER_ID);
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
        assertEquals(educationDto, result.getBody());
    }

    @Test
    public void testGetEducationByIdNotFound() {
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(educationService.findEducationByIdAndUserId(ID, USER_ID)).thenReturn(Optional.empty());

        var result = educationAPI.getEducationById(userDetails, ID, CANDIDATE_ID);

        verify(userDetails, times(2)).getUsername();
        verify(educationService, times(1)).findEducationByIdAndUserId(ID, USER_ID);
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(404), result.getStatusCode());
    }
    
    @Test
    public void testCreateEducationHappy() {
        var educationDto = getEducationDto();
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(educationService.createEducation(USER_ID, CANDIDATE_ID, educationDto)).thenReturn(Optional.of(educationDto));

        var result = educationAPI.createEducation(userDetails, educationDto, CANDIDATE_ID);

        verify(userDetails, times(2)).getUsername();
        verify(educationService, times(1)).createEducation(USER_ID, CANDIDATE_ID, educationDto);
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
        assertEquals(educationDto, result.getBody());
    }

    @Test
    public void testCreateEducationFailed() {
        var educationDto = getEducationDto();
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(educationService.createEducation(USER_ID, CANDIDATE_ID, educationDto)).thenReturn(Optional.empty());

        var result = educationAPI.createEducation(userDetails, educationDto, CANDIDATE_ID);

        verify(userDetails, times(2)).getUsername();
        verify(educationService, times(1)).createEducation(USER_ID, CANDIDATE_ID, educationDto);
        assertNotNull(result);
        assertEquals(HttpStatusCode.valueOf(500), result.getStatusCode());
    }

    @Test
    public void testUpdateEducation_Success() {
        
        var educationUpdateDTO = getEducationUpdateDto();
        var updatedEducationDTO = getEducationDto();
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(educationService.updateEducation(any(String.class), any(String.class), any(String.class), any(EducationUpdateDTO.class)))
                .thenReturn(Optional.of(updatedEducationDTO));

        
        var response = educationAPI.updateEducation(userDetails, educationUpdateDTO, ID, CANDIDATE_ID);

        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedEducationDTO, response.getBody());
    }

    @Test
    public void testUpdateEducation_Failure() {
        
        var educationUpdateDTO = getEducationUpdateDto();
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(educationService.updateEducation(any(String.class), any(String.class), any(String.class), any(EducationUpdateDTO.class)))
                .thenReturn(Optional.empty());

        
        var response = educationAPI.updateEducation(userDetails, educationUpdateDTO, ID, CANDIDATE_ID);

        
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testUpdateEducation_APIUpdateException() {
        
        var educationUpdateDTO = getEducationUpdateDto();
        APIUpdateException apiUpdateException = new APIUpdateException(HttpStatus.BAD_REQUEST);
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(educationService.updateEducation(any(String.class), any(String.class), any(String.class), any(EducationUpdateDTO.class)))
                .thenThrow(apiUpdateException);

        
        var response = educationAPI.updateEducation(userDetails, educationUpdateDTO, ID, CANDIDATE_ID);

        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeleteEducation_Success() {
        
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(educationService.deleteEducation(any(String.class), any(String.class)))
                .thenReturn(true);

        
        var response = educationAPI.deleteEducation(userDetails, ID);

        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeleteEducation_Failure() {
        
        when(userDetails.getUsername()).thenReturn(USER_ID);
        when(educationService.deleteEducation(any(String.class), any(String.class)))
                .thenReturn(false);

        
        var response = educationAPI.deleteEducation(userDetails, ID);

        
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    private EducationUpdateDTO getEducationUpdateDto() {
        return new EducationUpdateDTO(
                "BARK_NAME",
                LocalDate.now(),
                LocalDate.now(),
                true,
                "COMPUTER SCIENCE",
                EducationLevel.BACHELORS
        );
    }

    private EducationDTO getEducationDto() {
        return new EducationDTO(
                UUID.randomUUID().toString(),
                USER_ID,
                CANDIDATE_ID,
                "BARK_NAME",
                LocalDate.now(),
                LocalDate.now(),
                true,
                "COMPUTER SCIENCE",
                EducationLevel.BACHELORS
        );
    }


}