package com.gettimhired.it;

import com.gettimhired.TestHelper;
import com.gettimhired.config.TestSecurityConfig;
import com.gettimhired.model.dto.EducationDTO;
import com.gettimhired.model.dto.input.EducationInputDTO;
import com.gettimhired.model.dto.update.EducationUpdateDTO;
import com.gettimhired.model.mongo.EducationLevel;
import com.gettimhired.resolver.EducationResolver;
import com.gettimhired.service.EducationService;
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

@GraphQlTest(EducationResolver.class)
@AutoConfigureGraphQlTester
@Import(TestSecurityConfig.class)
class EducationResolverIT {

    @MockBean
    private EducationService educationService;

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
    void testGetEducations() {
        String candidateId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.findAllEducationsForUserAndCandidateId("user1", candidateId)).thenReturn(Collections.emptyList());

        graphQlTester.document("""
                        query {
                          getEducations(candidateId: "1", userId: "user1") {
                            id
                            userId
                            candidateId
                            name
                            startDate
                            endDate
                            graduated
                            areaOfStudy
                            educationLevel
                          }
                      }
                      """)
                .execute()
                .path("getEducations")
                .entityList(EducationDTO.class)
                .hasSize(0);

        verify(educationService, times(1)).findAllEducationsForUserAndCandidateId("user1", candidateId);
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetEducationById_Found() {
        String educationId = "1";
        EducationDTO education = new EducationDTO(educationId, "user1", "1", "School", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), true, "Barking", EducationLevel.DIPLOMA);
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.findEducationByIdAndUserId(educationId, "user1")).thenReturn(Optional.of(education));

        graphQlTester.document("""
                        query {
                          getEducationById(id: "1", userId: "user1") {
                               id
                               userId
                               candidateId
                               name
                               startDate
                               endDate
                               graduated
                               areaOfStudy
                               educationLevel
                             }
                        }
                        """)
                .execute()
                .path("getEducationById")
                .entity(EducationDTO.class)
                .isEqualTo(education);

        verify(educationService, times(1)).findEducationByIdAndUserId(educationId, "user1");
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetEducationById_NotFound() {
        String educationId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.findEducationByIdAndUserId(educationId, "user1")).thenReturn(Optional.empty());

        graphQlTester.document("""
                        query {
                          getEducationById(id: "1", userId: "user1") {
                               id
                               userId
                               candidateId
                               name
                               startDate
                               endDate
                               graduated
                               areaOfStudy
                               educationLevel
                             }
                        }
                        """)
                .execute()
                .path("getEducationById")
                .valueIsNull();

        verify(educationService, times(1)).findEducationByIdAndUserId(educationId, "user1");
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreateEducation_Success() {
        String candidateId = "1";
        EducationInputDTO educationInput = new EducationInputDTO(null, "user1", candidateId, "School", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), true, "Barking", EducationLevel.DIPLOMA);
        EducationDTO educationDTO = new EducationDTO("1", "user1", candidateId, "School", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), true, "Barking", EducationLevel.DIPLOMA);
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.createEducation(eq("user1"), eq(candidateId), any(EducationDTO.class))).thenReturn(Optional.of(educationDTO));

        graphQlTester.document("""
                        mutation {
                          createEducation(education: {
                            id:"",
                            userId:"",
                            candidateId:"1",
                            name: "No End Date School",
                            startDate: "1999-08-01",
                            endDate: null,
                            graduated: true,
                            educationLevel: "DIPLOMA",
                            areaOfStudy: "Fake"
                        }, userId: "user1") {
                            id
                            userId
                            candidateId
                            name
                            startDate
                            endDate
                            graduated
                            educationLevel
                            areaOfStudy
                          }
                        }
                        """)
                .execute();

        verify(educationService, times(1)).createEducation(eq("user1"), eq(candidateId), any(EducationDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testCreateEducation_Failure() {
        String candidateId = "1";
        EducationInputDTO educationInput = new EducationInputDTO(null, "user1", candidateId, "School", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), true, "Barking", EducationLevel.DIPLOMA);
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.createEducation(eq("user1"), eq(candidateId), any(EducationDTO.class))).thenReturn(Optional.empty());

        graphQlTester.document("""
                        mutation {
                           createEducation(education: {
                             id:"",
                             userId:"",
                             candidateId:"1",
                             name: "No End Date School",
                             startDate: "1999-08-01",
                             endDate: null,
                             graduated: true,
                             educationLevel: "DIPLOMA",
                             areaOfStudy: "Fake"
                         }, userId: "user1") {
                             id
                             userId
                             candidateId
                             name
                             startDate
                             endDate
                             graduated
                             educationLevel
                             areaOfStudy
                           }
                         }
                        """)
                .execute();

        verify(educationService, times(1)).createEducation(eq("user1"), eq(candidateId), any(EducationDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateEducation_Success() {
        String candidateId = "1";
        String educationId = "1";
        EducationInputDTO educationInput = new EducationInputDTO(educationId, "user1", candidateId, "Updated School", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), true, "Advanced Barking", EducationLevel.DIPLOMA);
        EducationDTO educationDTO = new EducationDTO(educationId, "user1", candidateId, "Updated School", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), true, "Advanced Barking", EducationLevel.DIPLOMA);
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.updateEducation(eq(educationId), eq("user1"), eq(candidateId), any(EducationUpdateDTO.class))).thenReturn(Optional.of(educationDTO));

        graphQlTester.document("""
                        mutation {
                           updateEducation(education: {
                             id:"1",
                             userId:"",
                             candidateId:"1",
                             name: "Bark School",
                             startDate: "2016-01-01",
                             endDate: "2017-01-01",
                             graduated: true,
                             educationLevel: "BACHELORS",
                             areaOfStudy: "Math"
                         }, userId: "user1", id: "1", candidateId: "1") {
                             id
                             userId
                             candidateId
                             name
                             startDate
                             endDate
                             graduated
                             educationLevel
                             areaOfStudy
                           }
                         }
                        """)
                .execute();

        verify(educationService, times(1)).updateEducation(eq(educationId), eq("user1"), eq(candidateId), any(EducationUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testUpdateEducation_Failure() {
        String candidateId = "1";
        String educationId = "1";
        EducationInputDTO educationInput = new EducationInputDTO(educationId, candidateId, TestHelper.CANDIDATE_ID,"NAME", LocalDate.parse("2021-01-01"), LocalDate.parse("2022-01-01"), true, "Advanced Barking", EducationLevel.DIPLOMA);
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.updateEducation(eq(educationId), eq("user1"), eq(candidateId), any(EducationUpdateDTO.class))).thenReturn(Optional.empty());

        graphQlTester.document("""
                        mutation {
                           updateEducation(education: {
                             id:"1",
                             userId:"",
                             candidateId:"1",
                             name: "Bark School",
                             startDate: "2016-01-01",
                             endDate: "2017-01-01",
                             graduated: true,
                             educationLevel: "BACHELORS",
                             areaOfStudy: "Math"
                         }, userId: "user1", id: "1", candidateId: "1") {
                             id
                             userId
                             candidateId
                             name
                             startDate
                             endDate
                             graduated
                             educationLevel
                             areaOfStudy
                           }
                         }
                        """)
                .execute()
                .path("updateEducation")
                .valueIsNull();

        verify(educationService, times(1)).updateEducation(eq(educationId), eq("user1"), eq(candidateId), any(EducationUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteEducation_Success() {
        String educationId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.deleteEducation(eq(educationId), eq("user1"))).thenReturn(true);

        graphQlTester.document("""
                        mutation {
                            deleteEducation(id: "1", userId: "user1")
                        }
                        """)
                .execute();

        verify(educationService, times(1)).deleteEducation(eq(educationId), eq("user1"));
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteEducation_Failure() {
        String educationId = "1";
        when(userDetails.getUsername()).thenReturn("user1");
        when(educationService.deleteEducation(eq(educationId), eq("user1"))).thenReturn(false);

        graphQlTester.document("""
                        mutation {
                            deleteEducation(id: "1", userId: "user1")
                        }
                        """)
                .execute();

        verify(educationService, times(1)).deleteEducation(eq(educationId), eq("user1"));
    }
}