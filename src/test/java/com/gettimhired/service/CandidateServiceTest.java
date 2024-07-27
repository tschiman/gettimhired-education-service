package com.gettimhired.service;

import com.gettimhired.TestHelper;
import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.CandidateDTO;
import com.gettimhired.model.dto.update.CandidateUpdateDTO;
import com.gettimhired.model.mongo.Candidate;
import com.gettimhired.repository.CandidateRepository;
import com.gettimhired.repository.EducationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CandidateServiceTest {

    private CandidateService candidateService;
    private CandidateRepository candidateRepository;
    private JobService jobService;
    private EducationRepository educationRepository;

    @BeforeEach
    public void init() {
        candidateRepository = mock(CandidateRepository.class);
        jobService = mock(JobService.class);
        educationRepository = mock(EducationRepository.class);
        candidateService = new CandidateService(candidateRepository, jobService, educationRepository);
    }

    @Test
    public void testFindAllCandidatesEmptyArrayList() {
        when(candidateRepository.findAllByUserIdOrderByLastName("BARK")).thenReturn(new ArrayList<>());

        var candidates = candidateService.findAllCandidatesForUser("BARK");

        verify(candidateRepository, times(1)).findAllByUserIdOrderByLastName("BARK");
        assertEquals(0, candidates.size());
    }

    @Test
    public void testFindCandidateByIdHappy() {
        var candidate = new Candidate(TestHelper.ID, TestHelper.USER_ID, "BARK_FNAME", "BARK_LNAME", "BARK_SUMMARY", "LinkedIn", "Github");
        when(candidateRepository.findCandidateByIdAndUserId(TestHelper.ID, TestHelper.USER_ID)).thenReturn(Optional.of(candidate));

        var result = candidateService.findCandidateByUserIdAndId(TestHelper.USER_ID, TestHelper.ID);

        verify(candidateRepository, times(1)).findCandidateByIdAndUserId(TestHelper.ID, TestHelper.USER_ID);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(TestHelper.ID, result.get().id());
        assertEquals(TestHelper.USER_ID, result.get().userId());
        assertEquals("BARK_FNAME", result.get().firstName());
        assertEquals("BARK_LNAME", result.get().lastName());
        assertEquals("BARK_SUMMARY", result.get().summary());
    }

    @Test
    public void testFindCandidateByIdNoCandidate() {
        when(candidateRepository.findCandidateByIdAndUserId(TestHelper.ID, TestHelper.USER_ID)).thenReturn(Optional.empty());

        var result = candidateService.findCandidateByUserIdAndId(TestHelper.USER_ID, TestHelper.ID);

        verify(candidateRepository, times(1)).findCandidateByIdAndUserId(TestHelper.ID, TestHelper.USER_ID);
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    public void testCreateCandidateHappy() {
        var candidateDtoIn = new CandidateDTO(null, null, "Bark", "McBarkson", "Summary Bark", "LinkedIn", "Github");
        var candidateOut = new Candidate(TestHelper.USER_ID, candidateDtoIn);
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidateOut);

        var candidateDtoOpt = candidateService.createCandidate(TestHelper.USER_ID, candidateDtoIn);

        verify(candidateRepository, times(1)).save(any(Candidate.class));
        assertTrue(candidateDtoOpt.isPresent());
        assertNotNull(candidateDtoOpt.get().id());
        assertNotNull(candidateDtoOpt.get().userId());
        assertEquals(TestHelper.USER_ID, candidateDtoOpt.get().userId());
        assertNotNull(candidateDtoOpt.get().firstName());
        assertNotNull(candidateDtoOpt.get().lastName());
        assertNotNull(candidateDtoOpt.get().summary());
    }

    @Test
    public void testCreateCandidateThrowsException() {
        var candidateDtoIn = new CandidateDTO(null, null, "Bark", "McBarkson", "Summary Bark", "LinkedIn", "Github");
        var candidateOut = new Candidate(TestHelper.USER_ID, candidateDtoIn);
        when(candidateRepository.save(any(Candidate.class))).thenThrow(new RuntimeException());

        var candidateDtoOpt = candidateService.createCandidate(TestHelper.USER_ID, candidateDtoIn);

        verify(candidateRepository, times(1)).save(any(Candidate.class));
        assertFalse(candidateDtoOpt.isPresent());
    }

    @Test
    public void testUpdateCandidateHappy() {
        var candidateUpdateDto = new CandidateUpdateDTO(
                "BARK_FNAME_UPDATE",
                "BARK_LNAME_UPDATE",
                "BARK_SUMMARY_UPDATE", "LinkedIn", "Github"
        );
        var candidate = new Candidate(
                TestHelper.ID,
                TestHelper.USER_ID,
                "BARK_FNAME",
                "BARK_LNAME",
                "BARK_SUMMARY", "LinkedIn", "Github"
        );
        when(candidateRepository.findById(TestHelper.ID)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any(Candidate.class))).then(AdditionalAnswers.returnsFirstArg());

        var candidateDtoOpt = candidateService.updateCandidate(TestHelper.ID, TestHelper.USER_ID, candidateUpdateDto);

        verify(candidateRepository, times(1)).findById(TestHelper.ID);
        verify(candidateRepository, times(1)).save(any(Candidate.class));
        assertTrue(candidateDtoOpt.isPresent());
        assertEquals(TestHelper.ID, candidateDtoOpt.get().id());
        assertEquals(TestHelper.USER_ID, candidateDtoOpt.get().userId());
        assertEquals("BARK_FNAME_UPDATE", candidateDtoOpt.get().firstName());
        assertEquals("BARK_LNAME_UPDATE", candidateDtoOpt.get().lastName());
        assertEquals("BARK_SUMMARY_UPDATE", candidateDtoOpt.get().summary());
    }

    @Test
    public void testUpdateCandidateDbErrorReturnOptionalEmpty() {
        var candidateUpdateDto = new CandidateUpdateDTO(
                "BARK_FNAME_UPDATE",
                "BARK_LNAME_UPDATE",
                "BARK_SUMMARY_UPDATE", "LinkedIn", "Github"
        );
        var candidate = new Candidate(
                TestHelper.ID,
                TestHelper.USER_ID,
                "BARK_FNAME",
                "BARK_LNAME",
                "BARK_SUMMARY", "LinkedIn", "Github"
        );
        when(candidateRepository.findById(TestHelper.ID)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any(Candidate.class))).thenThrow(new RuntimeException());

        var candidateDtoOpt = candidateService.updateCandidate(TestHelper.ID, TestHelper.USER_ID, candidateUpdateDto);

        verify(candidateRepository, times(1)).findById(TestHelper.ID);
        verify(candidateRepository, times(1)).save(any(Candidate.class));

        assertFalse(candidateDtoOpt.isPresent());
    }

    @Test
    public void testUpdateCandidateUserIdDoesNotMatch() {
        var candidateUpdateDto = new CandidateUpdateDTO(
                "BARK_FNAME_UPDATE",
                "BARK_LNAME_UPDATE",
                "BARK_SUMMARY_UPDATE", "LinkedIn", "Github"
        );
        var candidate = new Candidate(
                TestHelper.ID,
                "BARK_USER_ID_MISMATCH",
                "BARK_FNAME",
                "BARK_LNAME",
                "BARK_SUMMARY", "LinkedIn", "Github"
        );
        when(candidateRepository.findById(TestHelper.ID)).thenReturn(Optional.of(candidate));

        var ex = assertThrows(APIUpdateException.class, () -> candidateService.updateCandidate(TestHelper.ID, TestHelper.USER_ID, candidateUpdateDto));

        verify(candidateRepository, times(1)).findById(TestHelper.ID);
        verify(candidateRepository, times(0)).save(any(Candidate.class));
        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
    }

    @Test
    public void testUpdateCandidateNotFound() {
        var candidateUpdateDto = new CandidateUpdateDTO(
                "BARK_FNAME_UPDATE",
                "BARK_LNAME_UPDATE",
                "BARK_SUMMARY_UPDATE", "LinkedIn", "Github"
        );
        when(candidateRepository.findById(TestHelper.ID)).thenReturn(Optional.empty());

        var ex = assertThrows(APIUpdateException.class, () -> candidateService.updateCandidate(TestHelper.ID, TestHelper.USER_ID, candidateUpdateDto));

        verify(candidateRepository, times(1)).findById(TestHelper.ID);
        verify(candidateRepository, times(0)).save(any(Candidate.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    }

    @Test
    public void testDeleteCandidateHappy() {
        doNothing().when(candidateRepository).deleteByIdAndUserId(TestHelper.ID, TestHelper.USER_ID);
        doNothing().when(jobService).deleteJobsByCandidateIdAndUserId(TestHelper.ID, TestHelper.USER_ID);
        doNothing().when(educationRepository).deleteByUserId(TestHelper.USER_ID);

        var result = candidateService.deleteCandidate(TestHelper.ID, TestHelper.USER_ID);

        verify(candidateRepository, times(1)).deleteByIdAndUserId(TestHelper.ID, TestHelper.USER_ID);
        verify(jobService, times(1)).deleteJobsByCandidateIdAndUserId(TestHelper.ID, TestHelper.USER_ID);
        verify(educationRepository, times(1)).deleteByUserId(TestHelper.USER_ID);
        assertTrue(result);
    }

    @Test
    public void testDeleteCandidateTHrowsException() {
        doThrow(new RuntimeException()).when(candidateRepository).deleteByIdAndUserId(TestHelper.ID, TestHelper.USER_ID);

        var result = candidateService.deleteCandidate(TestHelper.ID, TestHelper.USER_ID);

        assertFalse(result);
    }

}