package com.gettimhired.controller;

import com.gettimhired.error.APIUpdateException;
import com.gettimhired.model.dto.CandidateDTO;
import com.gettimhired.model.dto.update.CandidateUpdateDTO;
import com.gettimhired.service.CandidateService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/candidates")
public class CandidateAPI {

    Logger log = LoggerFactory.getLogger(CandidateAPI.class);
    private final CandidateService candidateService;

    public CandidateAPI(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CandidateDTO> getAllCandidates(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("GET /api/candidates getAllCandidates userId={}", userDetails.getUsername());
        return candidateService.findAllCandidatesForUser(userDetails.getUsername());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CandidateDTO> getCandidateById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id
    ) {
        log.info("GET /api/candidates/{id} getCandidateById userId={} id={}", userDetails.getUsername(), id);
        var candidateOpt = candidateService.findCandidateByUserIdAndId(userDetails.getUsername(), id);
        return candidateOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CandidateDTO> createCandidate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CandidateDTO candidateDTO
    ) {
        log.info("POST /api/candidates createCandidate userId={}", userDetails.getUsername());
        var candidateDtoOpt = candidateService.createCandidate(userDetails.getUsername(), candidateDTO);
        return candidateDtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CandidateDTO> updateCandidate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CandidateUpdateDTO candidateUpdateDTO,
            @PathVariable String id
    ) {
        log.info("PUT /api/candidates/{id} updateCandidate userId={} id={}", userDetails.getUsername(), id);
        try {
            var candidateDtoOpt = candidateService.updateCandidate(id, userDetails.getUsername(), candidateUpdateDTO);
            return candidateDtoOpt
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (APIUpdateException e) {
            return ResponseEntity.status(e.getHttpStatus()).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity deleteCandidate(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id
    ) {
        log.info("DELETE /api/candidates/{id} deleteCandidate userId={} id={}", userDetails.getUsername(), id);
        boolean result = candidateService.deleteCandidate(id, userDetails.getUsername());
        return result ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
