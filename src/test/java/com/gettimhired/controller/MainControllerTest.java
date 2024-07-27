package com.gettimhired.controller;

import com.gettimhired.TestHelper;
import com.gettimhired.model.dto.CandidateDTO;
import com.gettimhired.model.dto.SignUpFormDTO;
import com.gettimhired.model.dto.UserDTO;
import com.gettimhired.service.CandidateService;
import com.gettimhired.service.EducationService;
import com.gettimhired.service.JobService;
import com.gettimhired.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MainControllerTest {

    private MainController mainController;
    private UserService userService;
    private Model model;
    private BindingResult bindingResult;
    private CandidateService candidateService;
    private EducationService educationService;
    private JobService jobService;
    private UserDetails userDetails;

    @BeforeEach
    public void init() {
        userService = mock(UserService.class);
        model = mock(Model.class);
        candidateService = mock(CandidateService.class);
        educationService = mock(EducationService.class);
        jobService = mock(JobService.class);
        bindingResult = mock(BindingResult.class);
        userDetails = mock(UserDetails.class);
        mainController = new MainController(userService, candidateService, educationService, jobService);
    }

    @Test
    public void testThatRootRouteReturnsTheIndexPage() {
        when(model.addAttribute(Mockito.anyString(), Mockito.anyList())).thenReturn(model);

        assertEquals("index", mainController.index(model));

        verify(model, times(1)).addAttribute(Mockito.anyString(), Mockito.anyList());
    }

    @Test
    public void testThatRootRouteWithCandidateReturnsTheIndex() {

        CandidateDTO candidateDto = new CandidateDTO(TestHelper.ID, TestHelper.USER_ID, null, null, null, null, null);
        when(candidateService.findCandidateById(TestHelper.ID)).thenReturn(Optional.of(candidateDto));
        when(model.addAttribute(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(model);
        when(model.addAttribute(Mockito.anyString(), Mockito.any())).thenReturn(model);
        when(model.addAttribute(Mockito.anyString(), Mockito.anyList())).thenReturn(model);
        when(model.addAttribute(Mockito.anyString(), Mockito.anyList())).thenReturn(model);

        assertEquals("index", mainController.index(TestHelper.ID, model));

        Mockito.verify(model, times(4)).addAttribute(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testThatAPIRouteReturnsTheAPIPage() {
        assertEquals("signups", mainController.signup(model));
    }

    @Test
    public void testThatPostmanRouteWorks() {
        assertEquals("postmans", mainController.postman());
    }

    @Test
    public void testSignUpPasswordDontMatch() {
        var signUpFormDto = new SignUpFormDTO("email", "password", "1password");
        doNothing().when(bindingResult).addError(any(ObjectError.class));
        when(bindingResult.hasErrors()).thenReturn(true);
        var result = mainController.signUp(signUpFormDto, bindingResult, model);

        assertEquals("signups", result);
        verify(bindingResult, times(1)).addError(any(ObjectError.class));
        verify(bindingResult, times(1)).hasErrors();
    }

    @Test
    public void testSignUpHappy() {
        var signUpFormDto = new SignUpFormDTO("email", "password", "password");
        when(bindingResult.hasErrors()).thenReturn(false);
        doNothing().when(userService).createUser(anyString(), anyString());
        var result = mainController.signUp(signUpFormDto, bindingResult, model);

        assertEquals("redirect:/login", result);
        verify(bindingResult, times(0)).addError(any(ObjectError.class));
        verify(bindingResult, times(1)).hasErrors();
        verify(userService, times(1)).createUser(anyString(), anyString());
    }

    @Test
    public void testGetAccountPage() {
        when(userDetails.getUsername()).thenReturn("userName");
        when(userDetails.getUsername()).thenReturn("userName");
        var user = new UserDTO("id", "pw", "email", "epw", Collections.emptyList());
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));

        var result = mainController.accountPage(userDetails, model);

        assertEquals("accounts", result);
        verify(userDetails, times(2)).getUsername();
        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    public void testGetAccountPageNoUser() {
        when(userDetails.getUsername()).thenReturn("userName");
        when(userDetails.getUsername()).thenReturn("userName");
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        var result = mainController.accountPage(userDetails, model);

        assertEquals("accounts", result);
        verify(userDetails, times(2)).getUsername();
        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    public void testCreateApiPasswordNoUser() {
        when(userDetails.getUsername()).thenReturn("userName");
        when(userDetails.getUsername()).thenReturn("userName");
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        var result = mainController.createApiPassword(userDetails, model);

        assertEquals("redirect:/error", result);
        verify(userDetails, times(2)).getUsername();
        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    public void testCreateApiPasswordWithUser() {
        when(userDetails.getUsername()).thenReturn("userName");
        when(userDetails.getUsername()).thenReturn("userName");
        var user = new UserDTO("id", "pw", "email", "epw", Collections.emptyList());
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userService.generatePassword(any(UserDTO.class))).thenReturn("pw");
        when(model.addAttribute(anyString(),anyString())).thenReturn(model);
        when(model.addAttribute(anyString(),anyString())).thenReturn(model);
        when(model.addAttribute(anyString(),anyString())).thenReturn(model);

        var result = mainController.createApiPassword(userDetails, model);

        assertEquals("accounts", result);
        verify(userDetails, times(2)).getUsername();
        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).generatePassword(any(UserDTO.class));
        verify(model, times(3)).addAttribute(anyString(),anyString());
    }
}