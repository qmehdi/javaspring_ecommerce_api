package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;

import static com.example.demo.SareetaApplicationTestsUtil.getUser;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestUserController {

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

    @Test
    public void findByIdTest() {

        // given
        when(principal.getName()).thenReturn("username123");
        when(userRepository.findById(getUser().getId())).thenReturn(java.util.Optional.of(getUser()));

        // when
        ResponseEntity<User> response = userController.findById(getUser().getId(), principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User actual = response.getBody();
        assertNotNull(actual);
    }

    @Test
    public void negativeFindByIdTestUnauthorized() {

        // given
        when(principal.getName()).thenReturn("usernam123");
        when(userRepository.findById(getUser().getId())).thenReturn(java.util.Optional.of(getUser()));

        // when
        ResponseEntity<User> response = userController.findById(1L, principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void negativeFindByUserIdTestNotFound() {

        // given
        when(principal.getName()).thenReturn("username123");
        when(userRepository.findById(getUser().getId())).thenReturn(java.util.Optional.of(getUser()));

        // when
        ResponseEntity<User> response = userController.findById(2L, principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        User actual = response.getBody();
        assertNull(actual);
    }

    @Test
    public void findUserByUsernameTest() {

        // given
        when(principal.getName()).thenReturn("username123");
        when(userRepository.findByUsername("username123")).thenReturn(getUser());

        // when
        ResponseEntity<User> response = userController.findByUserName("username123", principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User user = response.getBody();
        assertNotNull(user);
    }

    @Test
    public void findUserByUsernameTestUnauthorized() {

        // given
        when(principal.getName()).thenReturn("username123");
        when(userRepository.findByUsername("username123")).thenReturn(getUser());

        // when
        ResponseEntity<User> response = userController.findByUserName("username321", principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void findUserByUsernameTestNotFound() {

        // given
        when(principal.getName()).thenReturn("username321");
        when(userRepository.findByUsername("username123")).thenReturn(getUser());

        // when
        ResponseEntity<User> response = userController.findByUserName("username321", principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void createUserTest() {

        // given
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username123");
        createUserRequest.setPassword("password123");
        createUserRequest.setVerifyPassword("password123");
        when(userRepository.findByUsername(createUserRequest.getUsername())).thenReturn(null);
        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("passhash123");

        // when
        ResponseEntity<?> response = userController.createUser(createUserRequest);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User user = (User) response.getBody();
        assertNotNull(user);
        assertEquals(user.getPassword(), "passhash123");
        assertEquals(user.getUsername(), "username123");
    }

    @Test
    public void negativeCreateUserTestAlreadyExist() {

        // given
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username123");
        createUserRequest.setPassword("password123");
        createUserRequest.setVerifyPassword("password123");
        when(userRepository.findByUsername("username123")).thenReturn(getUser());

        // when
        ResponseEntity<?> response = userController.createUser(createUserRequest);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        User user = (User) response.getBody();
        assertNull(user);
    }

    @Test
    public void negativeCreateUserTestPasswordLengthIncorrect() {

        // given
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username123");
        createUserRequest.setPassword("pass");
        createUserRequest.setVerifyPassword("pass");
        when(userRepository.findByUsername("username123")).thenReturn(null);

        // when
        ResponseEntity<?> response = userController.createUser(createUserRequest);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void negativeCreateUserTestPasswordsNotEqual() {

        // given
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username123");
        createUserRequest.setPassword("password123");
        createUserRequest.setVerifyPassword("password321");
        when(userRepository.findByUsername("username123")).thenReturn(null);

        // when
        ResponseEntity<?> response = userController.createUser(createUserRequest);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
