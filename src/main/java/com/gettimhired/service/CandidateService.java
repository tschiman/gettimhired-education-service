package com.gettimhired.service;

import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.CandidateDTO;
import com.gettimhired.model.dto.update.CandidateUpdateDTO;
import com.gettimhired.model.mongo.Candidate;
import com.gettimhired.repository.CandidateRepository;
import com.gettimhired.repository.EducationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    Logger log = LoggerFactory.getLogger(CandidateService.class);
    private final CandidateRepository candidateRepository;
    private final JobService jobService;
    private final EducationRepository educationRepository;

    public CandidateService(CandidateRepository candidateRepository, JobService jobService, EducationRepository educationRepository) {
        this.candidateRepository = candidateRepository;
        this.jobService = jobService;
        this.educationRepository = educationRepository;
    }

    public List<CandidateDTO> findAllCandidatesForUser(String userId) {
        return candidateRepository.findAllByUserIdOrderByLastName(userId).stream()
                .map(CandidateDTO::new)
                .toList();
    }

    public Optional<CandidateDTO> findCandidateByUserIdAndId(String userId, String id) {
        return candidateRepository.findCandidateByIdAndUserId(id, userId).map(CandidateDTO::new);
    }

    public Optional<CandidateDTO> createCandidate(String userId, CandidateDTO candidateDTO) {
        var candidate = new Candidate(userId, candidateDTO);
        try {
            var candidateFromDb = candidateRepository.save(candidate);
            var candidateDtoFromDatabase = new CandidateDTO(candidateFromDb);
            return Optional.of(candidateDtoFromDatabase);
        } catch (Exception e) {
            log.error("createCandidate userId={}", userId, e);
            return Optional.empty();
        }
    }

    public Optional<CandidateDTO> updateCandidate(String id, String userId, CandidateUpdateDTO candidateUpdateDTO) {
        //get candidate from db
        Optional<Candidate> candidateOpt = candidateRepository.findById(id);
        if (candidateOpt.isPresent()) {
            //check if the username matches
            if (candidateOpt.get().userId().equals(userId)) {
                //then update the candidate values
                var candidateToSave = new Candidate(
                        candidateOpt.get().id(),
                        candidateOpt.get().userId(),
                        candidateUpdateDTO.firstName(),
                        candidateUpdateDTO.lastName(),
                        candidateUpdateDTO.summary(),
                        candidateUpdateDTO.linkedInUrl(),
                        candidateUpdateDTO.githubUrl()
                );
                Candidate candidateToReturn;
                try {
                    candidateToReturn = candidateRepository.save(candidateToSave);
                } catch (Exception e) {
                    log.error("updateCandidate userId={} id={}", userId, id, e);
                    return Optional.empty();
                }
                var candidateDto = new CandidateDTO(candidateToReturn);
                return Optional.of(candidateDto);
            } else {
                //userId does not match (403)
                throw new APIUpdateException(HttpStatus.FORBIDDEN);
            }
        } else {
            //CandidateId not found(404)
            throw new APIUpdateException(HttpStatus.NOT_FOUND);
        }
    }

    public boolean deleteCandidate(String id, String userId) {
        try {
            candidateRepository.deleteByIdAndUserId(id, userId);
            jobService.deleteJobsByCandidateIdAndUserId(id, userId);
            //TODO This is a bug, need to delete by UserId and CandidateId
            educationRepository.deleteByUserId(userId);
            return true;
        } catch (Exception e) {
            log.error("deleteCandidate userId={} id={}", userId, id, e);
            return false;
        }
    }

    public List<CandidateDTO> findAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(CandidateDTO::new)
                .toList();
    }

    public Optional<CandidateDTO> findCandidateById(String candidateId) {
        return candidateRepository.findById(candidateId)
                .map(CandidateDTO::new);
    }
}
