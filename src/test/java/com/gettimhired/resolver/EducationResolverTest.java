package com.gettimhired.resolver;

import com.gettimhired.model.dto.EducationDTO;
import com.gettimhired.model.dto.input.EducationInputDTO;
import com.gettimhired.model.dto.update.EducationUpdateDTO;
import com.gettimhired.model.mongo.EducationLevel;
import com.gettimhired.service.EducationService;
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

class EducationResolverTest {

    private EducationService educationService;
    private UserDetails userDetails;
    private EducationResolver educationResolver;

    @BeforeEach
    void setUp() {
        educationService = mock(EducationService.class);
        userDetails = mock(UserDetails.class);
        educationResolver = new EducationResolver(educationService);
    }

    @Test
    void testGetEducations() {
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.findAllEducationsForUserAndCandidateId("user1", "candidate1")).thenReturn(Collections.emptyList());

        List<EducationDTO> result = educationResolver.getEducations(userDetails, "candidate1", "user1");

        assertEquals(Collections.emptyList(), result);

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> candidateIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(educationService, times(1)).findAllEducationsForUserAndCandidateId(userIdCaptor.capture(), candidateIdCaptor.capture());
        assertEquals("user1", userIdCaptor.getValue());
        assertEquals("candidate1", candidateIdCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

    @Test
    void testGetEducationById() {
        EducationDTO education = new EducationDTO("1", "user1", "candidate1", "School", LocalDate.parse("2016-01-01"), LocalDate.parse("2017-01-01"), true, "BACHELORS", EducationLevel.DIPLOMA);
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.findEducationByIdAndUserId("1", "user1")).thenReturn(Optional.of(education));

        EducationDTO result = educationResolver.getEducationById(userDetails, "1", "user1");

        assertEquals(education, result);

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(educationService, times(1)).findEducationByIdAndUserId(idCaptor.capture(), userIdCaptor.capture());
        assertEquals("1", idCaptor.getValue());
        assertEquals("user1", userIdCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

    @Test
    void testCreateEducation() {
        EducationInputDTO educationInputDTO = new EducationInputDTO("1", "user1", "candidate1", "School", LocalDate.parse("2016-01-01"), LocalDate.parse("2017-01-01"), true, "BACHELORS", EducationLevel.DIPLOMA);
        EducationDTO educationDTO = new EducationDTO(educationInputDTO);
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.createEducation("user1", "candidate1", educationDTO)).thenReturn(Optional.of(educationDTO));

        EducationDTO result = educationResolver.createEducation(userDetails, educationInputDTO, "user1");


        assertEquals(educationDTO, result);

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> candidateIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<EducationDTO> educationCaptor = ArgumentCaptor.forClass(EducationDTO.class);
        verify(educationService, times(1)).createEducation(userIdCaptor.capture(), candidateIdCaptor.capture(), educationCaptor.capture());
        assertEquals("user1", userIdCaptor.getValue());
        assertEquals("candidate1", candidateIdCaptor.getValue());
        assertEquals(educationDTO, educationCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

    @Test
    void testUpdateEducation() {
        EducationInputDTO educationInputDTO = new EducationInputDTO("1", "user1", "candidate1", "School", LocalDate.parse("2016-01-01"), LocalDate.parse("2017-01-01"), true, "BACHELORS", EducationLevel.DIPLOMA);
        EducationUpdateDTO educationUpdateDTO = new EducationUpdateDTO(educationInputDTO);
        EducationDTO educationDTO = new EducationDTO(educationInputDTO);
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.updateEducation("1", "user1", "candidate1", educationUpdateDTO)).thenReturn(Optional.of(educationDTO));

        EducationDTO result = educationResolver.updateEducation(userDetails, educationInputDTO, "user1", "1", "candidate1");

        assertEquals(educationDTO, result);

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> candidateIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<EducationUpdateDTO> updateCaptor = ArgumentCaptor.forClass(EducationUpdateDTO.class);
        verify(educationService, times(1)).updateEducation(idCaptor.capture(), userIdCaptor.capture(), candidateIdCaptor.capture(), updateCaptor.capture());
        assertEquals("1", idCaptor.getValue());
        assertEquals("user1", userIdCaptor.getValue());
        assertEquals("candidate1", candidateIdCaptor.getValue());
        assertEquals(educationUpdateDTO, updateCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

    @Test
    void testDeleteEducation() {
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.deleteEducation("1", "user1")).thenReturn(true);

        boolean result = educationResolver.deleteEducation(userDetails, "1");

        assertEquals(true, result);

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(educationService, times(1)).deleteEducation(idCaptor.capture(), userIdCaptor.capture());
        assertEquals("1", idCaptor.getValue());
        assertEquals("user1", userIdCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

}