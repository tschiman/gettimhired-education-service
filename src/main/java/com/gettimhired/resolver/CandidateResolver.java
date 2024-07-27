package com.gettimhired.resolver;

import com.gettimhired.model.dto.CandidateDTO;
import com.gettimhired.model.dto.input.CandidateInputDTO;
import com.gettimhired.model.dto.update.CandidateUpdateDTO;
import com.gettimhired.service.CandidateService;
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
public class CandidateResolver {

    Logger log = LoggerFactory.getLogger(CandidateResolver.class);
    private final CandidateService candidateService;

    public CandidateResolver(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<CandidateDTO> getCandidates(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("GQL getCandidates userId={}", userDetails.getUsername());
        return candidateService.findAllCandidatesForUser(userDetails.getUsername());
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public CandidateDTO getCandidateById(@AuthenticationPrincipal UserDetails userDetails, @Argument String id) {
        log.info("GQL getCandidateById userId={} id={}", userDetails.getUsername(), id);
        return candidateService.findCandidateByUserIdAndId(userDetails.getUsername(), id).orElse(null);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public CandidateDTO createCandidate(@AuthenticationPrincipal UserDetails userDetails, @Argument @Valid CandidateInputDTO candidate) {
        log.info("GQL createCandidate userId={} id={}", userDetails.getUsername(), candidate.id());
        return candidateService.createCandidate(userDetails.getUsername(), new CandidateDTO(candidate)).orElse(null);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public CandidateDTO updateCandidate(@AuthenticationPrincipal UserDetails userDetails, @Argument @Valid CandidateInputDTO candidate) {
        log.info("GQL updateCandidate userId={} id={}", userDetails.getUsername(), candidate.id());
        return candidateService.updateCandidate(candidate.id(), userDetails.getUsername(), new CandidateUpdateDTO(candidate)).orElse(null);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public boolean deleteCandidate(@AuthenticationPrincipal UserDetails userDetails, @Argument @Valid String id) {
        log.info("GQL deleteCandidate userId={}", userDetails.getUsername());
        return candidateService.deleteCandidate(id, userDetails.getUsername());
    }
}
