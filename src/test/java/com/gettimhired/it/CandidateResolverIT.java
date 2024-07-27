package com.gettimhired.it;

import com.gettimhired.config.TestSecurityConfig;
import com.gettimhired.model.dto.CandidateDTO;
import com.gettimhired.model.dto.input.CandidateInputDTO;
import com.gettimhired.model.dto.update.CandidateUpdateDTO;
import com.gettimhired.resolver.CandidateResolver;
import com.gettimhired.service.CandidateService;
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

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

@GraphQlTest(CandidateResolver.class)
@AutoConfigureGraphQlTester
@Import(TestSecurityConfig.class)
class CandidateResolverIT {

    @MockBean
    private CandidateService candidateService;

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
    void testGetCandidates() throws Exception {
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.findAllCandidatesForUser("user1")).thenReturn(Collections.emptyList());

        graphQlTester.document("{ getCandidates { id userId firstName lastName summary } }")
                .execute()
                .path("getCandidates")
                .entityList(CandidateDTO.class)
                .hasSize(0);

        verify(candidateService, times(1)).findAllCandidatesForUser("user1");
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetCandidateById_Found() throws Exception {
        String candidateId = "1";
        CandidateDTO candidate = new CandidateDTO(candidateId, "user1", "First", "Last", "Summary", "LinkedIn", "Github");
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.findCandidateByUserIdAndId("user1", candidateId)).thenReturn(Optional.of(candidate));

        graphQlTester.document("{ getCandidateById(id: \"" + candidateId + "\") { id userId firstName lastName summary linkedInUrl githubUrl } }")
                .execute()
                .path("getCandidateById")
                .entity(CandidateDTO.class)
                .isEqualTo(candidate);

        verify(candidateService, times(1)).findCandidateByUserIdAndId("user1", candidateId);
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetCandidateById_NotFound() throws Exception {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.findCandidateByUserIdAndId("user1", candidateId)).thenReturn(Optional.empty());

        graphQlTester.document("{ getCandidateById(id: \"" + candidateId + "\") { id userId firstName lastName summary } }")
                .execute()
                .path("getCandidateById")
                .valueIsNull();

        verify(candidateService, times(1)).findCandidateByUserIdAndId("user1", candidateId);
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreateCandidate_Success() throws Exception {
        CandidateInputDTO candidateInput = new CandidateInputDTO(null,null,null,null,null, "LinkedIn", "Github");
        CandidateDTO candidateDTO = new CandidateDTO("1", "user1", "First", "Last", "Summary", "LinkedIn", "Github");
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.createCandidate(eq("user1"), any(CandidateDTO.class))).thenReturn(Optional.of(candidateDTO));

        graphQlTester.document("""
                        mutation {
                          createCandidate(candidate: {
                            id:"",
                            userId:"",
                            firstName: "Bark",
                            lastName: "BARK",
                            summary: "BARK"
                        }) {
                            id
                            userId
                            firstName
                            lastName
                            summary
                          }
                        }
                        """)
                .execute();

        verify(candidateService, times(1)).createCandidate(eq("user1"), any(CandidateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreateCandidate_Failure() throws Exception {
        CandidateInputDTO candidateInput = new CandidateInputDTO(null,null,null,null,null, "LinkedIn", "Github");
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.createCandidate(eq("user1"), any(CandidateDTO.class))).thenReturn(Optional.empty());

        graphQlTester.document("""
                        mutation {
                          createCandidate(candidate: {
                            id:"",
                            userId:"",
                            firstName: "Bark",
                            lastName: "BARK",
                            summary: "BARK"
                        }) {
                            id
                            userId
                            firstName
                            lastName
                            summary
                          }
                        }
                        """)
                .execute();

        verify(candidateService, times(1)).createCandidate(eq("user1"), any(CandidateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateCandidate_Success() throws Exception {
        CandidateInputDTO candidateInput = new CandidateInputDTO(null,null,null,null,null, "LinkedIn", "Github");
        CandidateDTO candidateDTO = new CandidateDTO("1", "user1", "First", "Last", "Summary", "LinkedIn", "Github");
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.updateCandidate(eq("1"), eq("user1"), any(CandidateUpdateDTO.class))).thenReturn(Optional.of(candidateDTO));

        graphQlTester.document("""
                                        mutation {
                                          updateCandidate(candidate: {
                                            id:"1",
                                            userId:"",
                                            firstName: "BARK",
                                            lastName: "BARK",
                                            summary: "BARK"
                                        }) {
                                            id
                                            userId
                                            firstName
                                            lastName
                                            summary
                                          }
                                        }
                        """
                )
                .execute();

        verify(candidateService, times(1)).updateCandidate(eq("1"), eq("user1"), any(CandidateUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateCandidate_Failure() throws Exception {
        CandidateInputDTO candidateInput = new CandidateInputDTO(null,null,null,null,null, "LinkedIn", "Github");
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.updateCandidate(eq("1"), eq("user1"), any(CandidateUpdateDTO.class))).thenReturn(Optional.empty());

        graphQlTester.document("""
                                        mutation {
                                          updateCandidate(candidate: {
                                            id:"1",
                                            userId:"",
                                            firstName: "BARK",
                                            lastName: "BARK",
                                            summary: "BARK"
                                        }) {
                                            id
                                            userId
                                            firstName
                                            lastName
                                            summary
                                          }
                                        }
                        """)
                .execute()
                .path("updateCandidate")
                .valueIsNull();

        verify(candidateService, times(1)).updateCandidate(eq("1"), eq("user1"), any(CandidateUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteCandidate_Success() throws Exception {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.deleteCandidate(eq(candidateId), eq("user1"))).thenReturn(true);

        graphQlTester.document("""
                        mutation {
                            deleteCandidate(id: "1")
                        }
                        """)
                .execute();

        verify(candidateService, times(1)).deleteCandidate(eq(candidateId), eq("user1"));
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteCandidate_Failure() throws Exception {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(candidateService.deleteCandidate(eq(candidateId), eq("user1"))).thenReturn(false);

        graphQlTester.document("""
                        mutation {
                            deleteCandidate(id: "1")
                        }
                        """)
                .execute();

        verify(candidateService, times(1)).deleteCandidate(eq(candidateId), eq("user1"));
    }
}