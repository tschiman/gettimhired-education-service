package com.gettimhired.repository;

import com.gettimhired.model.dto.EducationDTO;
import com.gettimhired.model.mongo.Education;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EducationRepository extends MongoRepository<Education, String> {
    List<Education> findAllByUserIdAndCandidateIdOrderByEndDate(String userId, String candidateId);

    Optional<Education> findEducationByIdAndUserId(String id, String userId);

    void deleteByIdAndUserId(String id, String userId);

    List<EducationDTO> findAllByCandidateId(String candidateId);

    void deleteByCandidateIdAndUserId(String candidateId, String userId);
}
