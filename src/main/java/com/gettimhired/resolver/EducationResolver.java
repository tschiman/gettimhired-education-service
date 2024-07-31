package com.gettimhired.resolver;

import com.gettimhired.model.dto.EducationDTO;
import com.gettimhired.model.dto.input.EducationInputDTO;
import com.gettimhired.model.dto.update.EducationUpdateDTO;
import com.gettimhired.service.EducationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class EducationResolver {

    Logger log = LoggerFactory.getLogger(EducationResolver.class);

    private final EducationService educationService;

    public EducationResolver(EducationService educationService) {
        this.educationService = educationService;
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<EducationDTO> getEducations(
            @AuthenticationPrincipal UserDetails userDetails,
            @Argument String candidateId,
            @Argument String userId
    ) {
        log.info("GQL getEducations userId={} candidateId={}", userId, candidateId);
        return educationService.findAllEducationsForUserAndCandidateId(userId, candidateId);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public EducationDTO getEducationById(@AuthenticationPrincipal UserDetails userDetails, @Argument String id, @Argument String userId) {
        log.info("GQL getEducationById userId={} id={}", userId, id);
        return educationService.findEducationByIdAndUserId(id, userId).orElse(null);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public EducationDTO createEducation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Argument @Valid EducationInputDTO education,
            @Argument String userId
    ) {
        log.info("GQL createEducation userId={} candidateId={}", userId, education.candidateId());
        return educationService.createEducation(userId, education.candidateId(), new EducationDTO(education)).orElse(null);
    }
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public EducationDTO updateEducation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Argument @Valid EducationInputDTO education,
            @Argument String userId,
            @Argument String id,
            @Argument String candidateId
    ) {
        log.info("GQL updateEducation userId={} candidateId={}", userId, candidateId);
        return educationService.updateEducation(id, userId, candidateId, new EducationUpdateDTO(education)).orElse(null);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public boolean deleteEducation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Argument String id,
            @Argument String userId
    ) {
        log.info("GQL deleteEducation userId={} id={}", userId, id);
        return educationService.deleteEducation(id, userId);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<EducationDTO> getEducationsByCandidateId(
            @AuthenticationPrincipal UserDetails userDetails,
            @Argument String candidateId
    ) {
        log.info("GQL getEducationsByCandidateId candidateId={}", candidateId);
        return educationService.findAllEducationsByCandidateId(candidateId);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public boolean deleteEducationByCandidateAndUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Argument String userId,
            @Argument String candidateId
    ) {
        log.info("GQL deleteEducationByCandidateAndUser userId={} candidateId={}", userId, candidateId);
        return educationService.deleteEducationByCandidate(candidateId, userId);
    }
}
