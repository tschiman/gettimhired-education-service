package com.gettimhired.it;

import com.gettimhired.controller.MainController;
import com.gettimhired.model.dto.CandidateDTO;
import com.gettimhired.model.dto.SignUpFormDTO;
import com.gettimhired.service.CandidateService;
import com.gettimhired.service.EducationService;
import com.gettimhired.service.JobService;
import com.gettimhired.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class)
class MainControllerIT {
    @MockBean
    private UserService userService;

    @MockBean
    private CandidateService candidateService;

    @MockBean
    private EducationService educationService;

    @MockBean
    private JobService jobService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new MainController(userService, candidateService, educationService, jobService)).build();
    }

    @Test
    void testIndex() throws Exception {
        when(candidateService.findAllCandidates()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("candidates"));

        verify(candidateService, times(1)).findAllCandidates();
    }

    @Test
    void testIndexWithCandidateId() throws Exception {
        String candidateId = "1";
        CandidateDTO candidate = new CandidateDTO(candidateId, "userId", null, null, null, "LinkedIn", "Github");
        when(candidateService.findCandidateById(candidateId)).thenReturn(Optional.of(candidate));
        when(educationService.findAllEducationsByCandidateId(candidateId)).thenReturn(Collections.emptyList());
        when(jobService.findAllJobsByCandidateId(candidateId, "userId")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/").param("candidateId", candidateId))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("hasCandidate"))
                .andExpect(model().attributeExists("candidate"))
                .andExpect(model().attributeExists("educations"))
                .andExpect(model().attributeExists("jobs"));

        verify(candidateService, times(1)).findCandidateById(candidateId);
        verify(educationService, times(1)).findAllEducationsByCandidateId(candidateId);
        verify(jobService, times(1)).findAllJobsByCandidateId(candidateId, "userId");
    }

    @Test
    void testSignupGet() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signups"))
                .andExpect(model().attributeExists("signUpForm"));
    }

    @Test
    void testSignupPostSuccess() throws Exception {
        SignUpFormDTO form = new SignUpFormDTO("test@example.com", "password", "password");
        doNothing().when(userService).createUser(form.email(), form.password());

        mockMvc.perform(post("/signup")
                        .param("email", form.email())
                        .param("password", form.password())
                        .param("passwordCopy", form.passwordCopy()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).createUser(form.email(), form.password());
    }

    @Test
    void testSignupPostPasswordMismatch() throws Exception {
        SignUpFormDTO form = new SignUpFormDTO("test@example.com", "password", "differentPassword");

        mockMvc.perform(post("/signup")
                        .param("email", form.email())
                        .param("password", form.password())
                        .param("passwordCopy", form.passwordCopy()))
                .andExpect(status().isOk())
                .andExpect(view().name("signups"));

        verify(userService, times(0)).createUser(anyString(), anyString());
    }

    @Test
    void testSignupPostValidationErrors() throws Exception {
        SignUpFormDTO form = new SignUpFormDTO("email", "password", "password");

        mockMvc.perform(post("/signup")
                        .param("email", form.email())
                        .param("password", form.password())
                        .param("passwordCopy", form.passwordCopy()))
                .andExpect(status().isOk())
                .andExpect(view().name("signups"));

        verify(userService, times(0)).createUser(anyString(), anyString());
    }

//    TODO Figure out how to test these with a mock spring security
//    @Test
//    @WithMockUser(username = "test@example.com")
//    void testAccountPage() throws Exception {
//        User user = new User("1", "pw", "test@example.com", "epw", Collections.emptyList());
//        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
//
//        mockMvc.perform(get("/account"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("accounts"))
//                .andExpect(model().attributeExists("user"))
//                .andExpect(model().attributeExists("email"));
//
//        verify(userService, times(1)).findByEmail("test@example.com");
//    }
//
//    @Test
//    @WithMockUser(username = "user1")
//    void testCreateApiPassword() throws Exception {
//        User user = new User("1", "pw", "test@example.com", "epw", Collections.emptyList());
//        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
//        when(userService.generatePassword(user)).thenReturn("newApiPassword");
//
//        mockMvc.perform(post("/account"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("accounts"))
//                .andExpect(model().attributeExists("user"))
//                .andExpect(model().attributeExists("email"))
//                .andExpect(model().attributeExists("password"));
//
//        verify(userService, times(1)).findByEmail("test@example.com");
//        verify(userService, times(1)).generatePassword(user);
//    }

    @Test
    void testPostman() throws Exception {
        mockMvc.perform(get("/postman"))
                .andExpect(status().isOk())
                .andExpect(view().name("postmans"));
    }
}