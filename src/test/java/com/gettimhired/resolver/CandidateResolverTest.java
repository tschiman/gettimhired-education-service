package com.gettimhired.resolver;

import com.gettimhired.model.dto.CandidateDTO;
import com.gettimhired.model.dto.input.CandidateInputDTO;
import com.gettimhired.model.dto.update.CandidateUpdateDTO;
import com.gettimhired.service.CandidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CandidateResolverTest {

    private CandidateService candidateService;
    private UserDetails userDetails;

    private CandidateResolver candidateResolver;

    @BeforeEach
    void setUp() {
        candidateService = mock(CandidateService.class);
        userDetails = mock(UserDetails.class);
        candidateResolver = new CandidateResolver(candidateService);
    }

    @Test
    void testGetCandidates() {
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.findAllCandidatesForUser("user1")).thenReturn(Collections.emptyList());

        List<CandidateDTO> result = candidateResolver.getCandidates(userDetails);

        assertEquals(Collections.emptyList(), result);

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(candidateService, times(1)).findAllCandidatesForUser(userIdCaptor.capture());
        assertEquals("user1", userIdCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

    @Test
    void testGetCandidateById() {
        CandidateDTO candidate = new CandidateDTO("1", "user1", "John", "Doe", "Summary", "LinkedIn", "Github");
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.findCandidateByUserIdAndId("user1", "1")).thenReturn(Optional.of(candidate));

        CandidateDTO result = candidateResolver.getCandidateById(userDetails, "1");

        assertEquals(candidate, result);

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(candidateService, times(1)).findCandidateByUserIdAndId(userIdCaptor.capture(), idCaptor.capture());
        assertEquals("user1", userIdCaptor.getValue());
        assertEquals("1", idCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

    @Test
    void testCreateCandidate() {
        CandidateInputDTO candidateInputDTO = new CandidateInputDTO("id","1", "John", "Doe", "Summary", "LinkedIn", "Github");
        CandidateDTO candidateDTO = new CandidateDTO(candidateInputDTO);
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.createCandidate("user1", candidateDTO)).thenReturn(Optional.of(candidateDTO));

        CandidateDTO result = candidateResolver.createCandidate(userDetails, candidateInputDTO);

        assertEquals(candidateDTO, result);

        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CandidateDTO> candidateCaptor = ArgumentCaptor.forClass(CandidateDTO.class);
        verify(candidateService, times(1)).createCandidate(userIdCaptor.capture(), candidateCaptor.capture());
        assertEquals("user1", userIdCaptor.getValue());
        assertEquals(candidateDTO, candidateCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

    @Test
    void testUpdateCandidate() {
        CandidateInputDTO candidateInputDTO = new CandidateInputDTO("ID","1", "John", "Doe", "Summary", "LinkedIn", "Github");
        CandidateUpdateDTO candidateUpdateDTO = new CandidateUpdateDTO(candidateInputDTO);
        CandidateDTO candidateDTO = new CandidateDTO(candidateInputDTO);
        when(userDetails.getUsername()).thenReturn("1");
        when(candidateService.updateCandidate("ID", "1", candidateUpdateDTO)).thenReturn(Optional.of(candidateDTO));

        CandidateDTO result = candidateResolver.updateCandidate(userDetails, candidateInputDTO);

        assertEquals(candidateDTO, result);

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CandidateUpdateDTO> updateCaptor = ArgumentCaptor.forClass(CandidateUpdateDTO.class);
        verify(candidateService, times(1)).updateCandidate(idCaptor.capture(), userIdCaptor.capture(), updateCaptor.capture());
        assertEquals("ID", idCaptor.getValue());
        assertEquals("1", userIdCaptor.getValue());
        assertEquals(candidateUpdateDTO, updateCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }

    @Test
    void testDeleteCandidate() {
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.deleteCandidate("1", "user1")).thenReturn(true);

        boolean result = candidateResolver.deleteCandidate(userDetails, "1");

        assertEquals(true, result);

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(candidateService, times(1)).deleteCandidate(idCaptor.capture(), userIdCaptor.capture());
        assertEquals("1", idCaptor.getValue());
        assertEquals("user1", userIdCaptor.getValue());
        verify(userDetails, times(2)).getUsername();
    }
}