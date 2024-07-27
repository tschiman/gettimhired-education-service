package com.gettimhired.resolver;

import com.gettimhired.model.dto.JobDTO;
import com.gettimhired.model.dto.input.JobInputDTO;
import com.gettimhired.model.dto.update.JobUpdateDTO;
import com.gettimhired.service.JobService;
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
public class JobResolver {

    Logger log = LoggerFactory.getLogger(JobResolver.class);
    private final JobService jobService;

    public JobResolver(JobService jobService) {
        this.jobService = jobService;
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<JobDTO> getJobs(@AuthenticationPrincipal UserDetails userDetails, @Argument String candidateId) {
        log.info("GQL getJobs userId={} candidateId={}", userDetails.getUsername(), candidateId);
        return jobService.findAllJobsForUserAndCandidateId(userDetails.getUsername(), candidateId);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public JobDTO getJobById(@AuthenticationPrincipal UserDetails userDetails, @Argument String id, @Argument String candidateId) {
        log.info("GQL getJobById userId={} id={}", userDetails.getUsername(), id);
        return jobService.findJobByIdAndUserId(id, userDetails.getUsername(), candidateId).orElse(null);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public JobDTO createJob(@AuthenticationPrincipal UserDetails userDetails, @Argument @Valid JobInputDTO job) {
        log.info("GQL createJob userId={} candidateId={}", userDetails.getUsername(), job.candidateId());
        return jobService.createJob(userDetails.getUsername(), job.candidateId(), new JobDTO(job)).orElse(null);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public JobDTO updateJob(@AuthenticationPrincipal UserDetails userDetails, @Argument @Valid JobInputDTO job) {
        log.info("GQL updateJob userId={} candidateId={}", userDetails.getUsername(), job.candidateId());
        return jobService.updateJob(job.id(), userDetails.getUsername(), job.candidateId(), new JobUpdateDTO(job)).orElse(null);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public boolean deleteJob(@AuthenticationPrincipal UserDetails userDetails, @Argument String id, @Argument String candidateId) {
        log.info("GQL deleteJob userId={} id={}", userDetails.getUsername(), id);
        return jobService.deleteJob(id, userDetails.getUsername(), candidateId);
    }
}
