package com.gettimhired.service;

import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.EducationDTO;
import com.gettimhired.model.dto.update.EducationUpdateDTO;
import com.gettimhired.model.mongo.Education;
import com.gettimhired.repository.EducationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EducationService {

    Logger log = LoggerFactory.getLogger(EducationService.class);
    private final EducationRepository educationRepository;

    public EducationService(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    public List<EducationDTO> findAllEducationsForUserAndCandidateId(String userId, String candidateId) {
        return educationRepository.findAllByUserIdAndCandidateIdOrderByEndDate(userId, candidateId).stream()
                .map(EducationDTO::new)
                .toList();
    }

    public Optional<EducationDTO> findEducationByIdAndUserId(String id, String userId) {
        return educationRepository.findEducationByIdAndUserId(id, userId).map(EducationDTO::new);
    }

    public Optional<EducationDTO> createEducation(String userId, String candidateId, EducationDTO educationDTO) {
        var education = new Education(userId, candidateId, educationDTO);
        try {
            var educationFromDb = educationRepository.save(education);
            var educationDtoFromDatabase = new EducationDTO(educationFromDb);
            return Optional.of(educationDtoFromDatabase);
        } catch (Exception e) {
            log.error("createEducation userId={}", userId, e);
            return Optional.empty();
        }
    }

    public Optional<EducationDTO> updateEducation(String id, String userId, String candidateId, EducationUpdateDTO educationUpdateDTO) {
        //get education from db
        Optional<Education> educationOpt = educationRepository.findById(id);
        if (educationOpt.isPresent()) {
            //check if the username matches
            if (educationOpt.get().userId().equals(userId)) {
                //check the candidateId
                if (educationOpt.get().candidateId().equals(candidateId)) {
                    //then update the candidate values
                    var educationToSave = new Education(
                            educationOpt.get().id(),
                            educationOpt.get().userId(),
                            educationOpt.get().candidateId(),
                            educationUpdateDTO.name(),
                            educationUpdateDTO.startDate(),
                            educationUpdateDTO.endDate(),
                            educationUpdateDTO.graduated(),
                            educationUpdateDTO.areaOfStudy(),
                            educationUpdateDTO.educationLevel()
                    );
                    Education educationToReturn;
                    try {
                        educationToReturn = educationRepository.save(educationToSave);
                    } catch (Exception e) {
                        log.error("updateEducation userId={} id={} candidateId={}", userId, id, candidateId, e);
                        return Optional.empty();
                    }
                    var educationDTO = new EducationDTO(educationToReturn);
                    return Optional.of(educationDTO);
                } else {
                    throw new APIUpdateException(HttpStatus.FORBIDDEN);
                }
            } else {
                //userId does not match (403)
                throw new APIUpdateException(HttpStatus.FORBIDDEN);
            }
        } else {
            //CandidateId not found(404)
            throw new APIUpdateException(HttpStatus.NOT_FOUND);
        }
    }

    public boolean deleteEducation(String id, String userId) {
        try {
            educationRepository.deleteByIdAndUserId(id, userId);
            return true;
        } catch (Exception e) {
            log.error("deleteEducation userId={} id={}", userId, id, e);
            return false;
        }
    }

    public List<EducationDTO> findAllEducationsByCandidateId(String candidateId) {
        return educationRepository.findAllByCandidateId(candidateId)
                .stream().sorted((e1, e2) -> {
                    if (e1.endDate() == null && e2.endDate() == null) {
                        return 0;
                    }
                    if (e1.endDate() == null) {
                        return -1;
                    }
                    if (e2.endDate() == null) {
                        return 1;
                    }
                    return e2.endDate().compareTo(e1.endDate());
                }).toList();
    }
}
