package com.example.demo.exceptions;

import com.example.demo.controllers.UserController;
import com.example.demo.exceptions.SareetaExceptionHandler;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestExceptionHandler {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Principal principal;

    private MockMvc mockMvc;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(new SareetaExceptionHandler())
                .build();
    }

    @Test
    public void exceptionHandlerTest() throws Exception {

        // given
        when(principal.getName()).thenReturn("username123");
        when(userController.findById(1l, principal)).thenThrow(new RuntimeException(new Exception()));

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/user/id/1"));

        // then
        resultActions.andExpect(status().is(500));
    }

}

