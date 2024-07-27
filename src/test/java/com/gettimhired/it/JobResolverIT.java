package com.gettimhired.it;

import com.gettimhired.config.TestSecurityConfig;
import com.gettimhired.model.dto.JobDTO;
import com.gettimhired.model.dto.input.JobInputDTO;
import com.gettimhired.model.dto.update.JobUpdateDTO;
import com.gettimhired.resolver.JobResolver;
import com.gettimhired.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@GraphQlTest(JobResolver.class)
@AutoConfigureGraphQlTester
@Import(TestSecurityConfig.class)
class JobResolverIT {

    @MockBean
    private JobService jobService;

    @Mock
    private UserDetails userDetails;

    @Autowired
    private GraphQlTester graphQlTester;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetJobs() {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.findAllJobsForUserAndCandidateId("user1", candidateId)).thenReturn(Collections.emptyList());

        graphQlTester.document("""
                          query {
                              getJobs(candidateId: "1") {
                                id
                                userId
                                candidateId
                                companyName
                                title
                                startDate
                                endDate
                                skills
                                achievements
                                currentlyWorking
                                reasonForLeaving
                              }
                            }
                        """)
                .execute()
                .path("getJobs")
                .entityList(JobDTO.class)
                .hasSize(0);

        verify(jobService, times(1)).findAllJobsForUserAndCandidateId("user1", candidateId);
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetJobById_Found() {
        String jobId = "1";
        JobDTO jobDTO = new JobDTO(jobId, "user1", "", "Updated Title", "Updated Company", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), null, null,true, "Leaving");
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.findJobByIdAndUserId(jobId, "user1", "candidateId")).thenReturn(Optional.of(jobDTO));

        graphQlTester.document("""
                        query {
                           getJobById(id: "1") {
                             id
                             userId
                             candidateId
                             companyName
                             title
                             startDate
                             endDate
                             skills
                             achievements
                             currentlyWorking
                             reasonForLeaving
                           }
                         }
                        """)
                .execute()
                .path("getJobById")
                .entity(JobDTO.class)
                .isEqualTo(jobDTO);

        verify(jobService, times(1)).findJobByIdAndUserId(jobId, "user1", "candidateId");
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetJobById_NotFound() {
        String jobId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.findJobByIdAndUserId(jobId, "user1", "candidateId")).thenReturn(Optional.empty());

        graphQlTester.document("""
                        query {
                           getJobById(id: "1") {
                             id
                             userId
                             candidateId
                             companyName
                             title
                             startDate
                             endDate
                             skills
                             achievements
                             currentlyWorking
                             reasonForLeaving
                           }
                         }
                        """)
                .execute()
                .path("getJobById")
                .valueIsNull();

        verify(jobService, times(1)).findJobByIdAndUserId(jobId, "user1", "candidateId");
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreateJob_Success() {
        String candidateId = "1";
        JobDTO jobDTO = new JobDTO("", "user1", candidateId, "Updated Title", "Updated Company", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), null, null,true, "Leaving");
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.createJob(eq("user1"), eq(candidateId), any(JobDTO.class))).thenReturn(Optional.of(jobDTO));

        graphQlTester.document("""
                        mutation {
                           createJob(job: {
                             id:"",
                             userId:"",
                             candidateId:"1",
                             companyName: "Name",
                             title: "Java Developer",
                             startDate: "2014-07-01",
                             endDate: "2015-06-01",
                             skills: ["BARK"],
                             achievements: ["BARK"],
                             currentlyWorking: false,
                             reasonForLeaving: "BARK"
                             }) {
                             id
                             userId
                             candidateId
                             companyName
                             title
                             startDate
                             endDate
                             skills
                             achievements
                             currentlyWorking
                             reasonForLeaving
                           }
                         }
                        """)
                .execute();

        verify(jobService, times(1)).createJob(eq("user1"), eq(candidateId), any(JobDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreateJob_Failure() {
        String candidateId = "1";
        JobInputDTO jobInput = new JobInputDTO("", "user1", candidateId, "Updated Title", "Updated Company", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), null,null,true, "Leaving");
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.createJob(eq("user1"), eq(candidateId), any(JobDTO.class))).thenReturn(Optional.empty());

        graphQlTester.document("""
                        mutation {
                           createJob(job: {
                             id:"",
                             userId:"",
                             candidateId:"1",
                             companyName: "Name",
                             title: "Java Developer",
                             startDate: "2014-07-01",
                             endDate: "2015-06-01",
                             skills: ["BARK"],
                             achievements: ["BARK"],
                             currentlyWorking: false,
                             reasonForLeaving: "BARK"
                             }) {
                             id
                             userId
                             candidateId
                             companyName
                             title
                             startDate
                             endDate
                             skills
                             achievements
                             currentlyWorking
                             reasonForLeaving
                           }
                         }
                        """)
                .execute();

        verify(jobService, times(1)).createJob(eq("user1"), eq(candidateId), any(JobDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateJob_Success() {
        String candidateId = "1";
        String jobId = "1";
        JobInputDTO jobInput = new JobInputDTO(jobId, "user1", candidateId, "Updated Title", "Updated Company", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), null,null,true, "Leaving");
        JobDTO jobDTO = new JobDTO(jobId, "user1", candidateId, "Updated Title", "Updated Company", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), null, null,true, "Leaving");
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.updateJob(eq(jobId), eq("user1"), eq(candidateId), any(JobUpdateDTO.class))).thenReturn(Optional.of(jobDTO));

        graphQlTester.document("""
                        mutation {
                           updateJob(job: {
                             id:"1",
                             userId:"",
                             candidateId:"1",
                             companyName: "BARK",
                             title: "BARK",
                             startDate: "2023-09-01",
                             endDate: "2023-11-01",
                             skills: [
                                 "BARK"
                             ],
                             achievements: [
                                 "BARK"
                             ],
                             currentlyWorking: false,
                             reasonForLeaving: "BARK"
                         }) {
                             id
                             userId
                             candidateId
                             companyName
                             title
                             startDate
                             endDate
                             skills
                             achievements
                             currentlyWorking
                             reasonForLeaving
                           }
                         }
                        """)
                .execute();

        verify(jobService, times(1)).updateJob(eq(jobId), eq("user1"), eq(candidateId), any(JobUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateJob_Failure() {
        String candidateId = "1";
        String jobId = "1";
        JobInputDTO jobInput = new JobInputDTO(jobId, "user1", candidateId, "Updated Title", "Updated Company", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), null,null,true, "Leaving");
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.updateJob(eq(jobId), eq("user1"), eq(candidateId), any(JobUpdateDTO.class))).thenReturn(Optional.empty());

        graphQlTester.document("""
                        mutation {
                           updateJob(job: {
                             id:"1",
                             userId:"",
                             candidateId:"1",
                             companyName: "BARK",
                             title: "BARK",
                             startDate: "2023-09-01",
                             endDate: "2023-11-01",
                             skills: [
                                 "BARK"
                             ],
                             achievements: [
                                 "BARK"
                             ],
                             currentlyWorking: false,
                             reasonForLeaving: "BARK"
                         }) {
                             id
                             userId
                             candidateId
                             companyName
                             title
                             startDate
                             endDate
                             skills
                             achievements
                             currentlyWorking
                             reasonForLeaving
                           }
                         }
                        """)
                .execute()
                .path("updateJob")
                .valueIsNull();

        verify(jobService, times(1)).updateJob(eq(jobId), eq("user1"), eq(candidateId), any(JobUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteJob_Success() {
        String jobId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.deleteJob(eq(jobId), eq("user1"), eq("candidateId"))).thenReturn(true);

        graphQlTester.document("""
                        mutation {
                            deleteJob(id: "1")
                        }
                        """)
                .execute();

        verify(jobService, times(1)).deleteJob(eq(jobId), eq("user1"), eq("candidateId"));
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteJob_Failure() {
        String jobId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(jobService.deleteJob(eq(jobId), eq("user1"), eq("candidateId"))).thenReturn(false);

        graphQlTester.document("""
                        mutation {
                            deleteJob(id: "1")
                        }
                        """)
                .execute();

        verify(jobService, times(1)).deleteJob(eq(jobId), eq("user1"), eq("candidateId"));
    }
}