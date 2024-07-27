package com.gettimhired.service;

import com.gettimhired.TestHelper;
import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.EducationDTO;
import com.gettimhired.model.dto.update.EducationUpdateDTO;
import com.gettimhired.model.mongo.Education;
import com.gettimhired.model.mongo.EducationLevel;
import com.gettimhired.repository.EducationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gettimhired.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EducationServiceTest {
    private EducationService educationService;
    private EducationRepository educationRepository;

    @BeforeEach
    public void init() {
        educationRepository = mock(EducationRepository.class);
        educationService = new EducationService(educationRepository);
    }

    @Test
    public void testfindAllEducationsForUserAndCandidateIdHappy() {
        var e1 = getEducation("BARK_NAME");
        var e2 = getEducation("BARK_NAME_TWO");
        var educations = List.of(e1, e2);
        when(educationRepository.findAllByUserIdAndCandidateIdOrderByEndDate(TestHelper.USER_ID, TestHelper.CANDIDATE_ID)).thenReturn(educations);

        var result = educationService.findAllEducationsForUserAndCandidateId(TestHelper.USER_ID, TestHelper.CANDIDATE_ID);

        verify(educationRepository, times(1)).findAllByUserIdAndCandidateIdOrderByEndDate(TestHelper.USER_ID, TestHelper.CANDIDATE_ID);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testFindEducationByUserIdAndCandidateIdAndId_Found() {
        
        var education = getEducation("BARK_NAME");
        var expectedEducationDTO = new EducationDTO(education);
        when(educationRepository.findEducationByIdAndUserId(anyString(), anyString()))
                .thenReturn(Optional.of(education));

        
        Optional<EducationDTO> result = educationService.findEducationByIdAndUserId(TestHelper.ID, TestHelper.USER_ID);

        
        assertTrue(result.isPresent());
        assertEquals(expectedEducationDTO, result.get());
    }

    @Test
    public void testFindEducationByUserIdAndCandidateIdAndId_NotFound() {
        
        when(educationRepository.findEducationByIdAndUserId(anyString(), anyString()))
                .thenReturn(Optional.empty());

        
        Optional<EducationDTO> result = educationService.findEducationByIdAndUserId(ID, USER_ID);


        assertFalse(result.isPresent());
    }

    @Test
    public void testCreateEducation_Success() {
        var education = getEducation("BARK_NAME");;
        EducationDTO educationDTO = new EducationDTO(education);
        Education savedEducation = getEducation("BARK_NAME"); // Assuming you have an Education entity
        when(educationRepository.save(any(Education.class))).thenReturn(savedEducation);

        Optional<EducationDTO> result = educationService.createEducation(TestHelper.USER_ID, TestHelper.CANDIDATE_ID, educationDTO);

        assertTrue(result.isPresent());
        assertEquals(new EducationDTO(savedEducation), result.get());
    }

    @Test
    public void testCreateEducation_Failure() {
        EducationDTO educationDTO = new EducationDTO(getEducation("BARK_NAME"));
        when(educationRepository.save(any(Education.class))).thenThrow(new RuntimeException("Database error"));

        Optional<EducationDTO> result = educationService.createEducation(TestHelper.USER_ID, TestHelper.CANDIDATE_ID, educationDTO);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateEducation_EducationNotFound() {
        var educationUpdateDto = getEducationUpdate();
        when(educationRepository.findById(TestHelper.ID)).thenReturn(Optional.empty());

        var ex = assertThrows(APIUpdateException.class, () -> educationService.updateEducation(TestHelper.ID, TestHelper.USER_ID, TestHelper.CANDIDATE_ID, educationUpdateDto));

        verify(educationRepository, times(1)).findById(TestHelper.ID);
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    }

    @Test
    public void testUpdateEducation_UserIdNotMatch() {
        var education = getEducation("BARK_NAME");
        var educationUpdateDto = getEducationUpdate();
        when(educationRepository.findById(TestHelper.ID)).thenReturn(Optional.of(education));

        var ex = assertThrows(APIUpdateException.class, () -> educationService.updateEducation(TestHelper.ID, "NO_MATCH", TestHelper.CANDIDATE_ID, educationUpdateDto));

        verify(educationRepository, times(1)).findById(TestHelper.ID);
        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
    }

    @Test
    public void testUpdateEducation_CandidateIdNotMatch() {
        var education = getEducation("BARK_NAME");
        var educationUpdateDto = getEducationUpdate();
        when(educationRepository.findById(TestHelper.ID)).thenReturn(Optional.of(education));

        var ex = assertThrows(APIUpdateException.class, () -> educationService.updateEducation(TestHelper.ID, TestHelper.USER_ID, "NO_MATCH", educationUpdateDto));

        verify(educationRepository, times(1)).findById(TestHelper.ID);
        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
    }

    @Test
    public void testUpdateEducation_SaveThrowsException() {
        var education = getEducation("BARK_NAME");
        var educationUpdateDto = getEducationUpdate();
        when(educationRepository.findById(TestHelper.ID)).thenReturn(Optional.of(education));
        when(educationRepository.save(any(Education.class))).thenThrow(new RuntimeException());

        var result = educationService.updateEducation(TestHelper.ID, TestHelper.USER_ID, TestHelper.CANDIDATE_ID, educationUpdateDto);

        verify(educationRepository, times(1)).findById(TestHelper.ID);
        verify(educationRepository, times(1)).save(any(Education.class));
        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateEducation_Happy() {
        var education = getEducation("BARK_NAME");
        var educationUpdateDto = getEducationUpdate();
        when(educationRepository.findById(TestHelper.ID)).thenReturn(Optional.of(education));
        when(educationRepository.save(any(Education.class))).thenReturn(education);

        var result = educationService.updateEducation(TestHelper.ID, TestHelper.USER_ID, TestHelper.CANDIDATE_ID, educationUpdateDto);

        verify(educationRepository, times(1)).findById(TestHelper.ID);
        verify(educationRepository, times(1)).save(any(Education.class));
        assertTrue(result.isPresent());
    }

    @Test
    public void testDeleteEducation_Success() {
        doNothing().when(educationRepository).deleteByIdAndUserId(TestHelper.ID, TestHelper.USER_ID);

        boolean result = educationService.deleteEducation(TestHelper.ID, TestHelper.USER_ID);

        assertTrue(result);
    }

    @Test
    public void testDeleteEducation_Failure() {
        doThrow(new RuntimeException("Database error")).when(educationRepository).deleteByIdAndUserId(TestHelper.ID, TestHelper.USER_ID);

        boolean result = educationService.deleteEducation(TestHelper.ID, TestHelper.USER_ID);

        assertFalse(result);
    }

    @Test
    public void testFindAllEducationsByCandidateId_Sorting() {

        var e1 = new EducationDTO(null,null,null,null,null,LocalDate.of(2000,1,1),null,null,null);
        var e2 = new EducationDTO(null,null,null,null,null,LocalDate.of(2020,1,1),null,null,null);
        var e3 = new EducationDTO(null,null,null,null,null,null,null,null,null);
        var educations = List.of(e1, e2, e3);
        when(educationRepository.findAllByCandidateId(CANDIDATE_ID)).thenReturn(educations);

        var result = educationService.findAllEducationsByCandidateId(CANDIDATE_ID);

        assertEquals(3, result.size());
        assertEquals(e3, result.get(0));
        assertEquals(e2, result.get(1));
        assertEquals(e1, result.get(2));
    }

    private static Education getEducation(String name) {
        return new Education(
                UUID.randomUUID().toString(),
                TestHelper.USER_ID,
                TestHelper.CANDIDATE_ID,
                name,
                LocalDate.now(),
                LocalDate.now(),
                true,
                "Computer Science",
                EducationLevel.BACHELORS
        );
    }

    private EducationUpdateDTO getEducationUpdate() {
        return new EducationUpdateDTO(
                "BARK_NAME",
                LocalDate.now(),
                LocalDate.now(),
                true,
                "Computer Science",
                EducationLevel.BACHELORS
        );
    }

}