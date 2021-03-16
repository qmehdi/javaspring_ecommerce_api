package com.example.demo.controllers;


import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

import static com.example.demo.SareetaApplicationTestsUtil.getUser;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TestOrderController {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private Principal principal;

    @Test
    public void submitOrderTest() {

        // given
        when(principal.getName()).thenReturn("username123");
        when(userRepository.findByUsername("username123")).thenReturn(getUser());

        // when
        ResponseEntity<UserOrder> response = orderController.submit("username123", principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserOrder order = response.getBody();
        assertNotNull(order);
        User user = getUser();
        assertEquals(order.getUser().getUsername(), user.getUsername());

    }

    @Test
    public void negativeSubmitOrderTestUserUnauthorized() {

        // given
        when(principal.getName()).thenReturn("username123");

        // when
        ResponseEntity<UserOrder> response = orderController.submit("username321", principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        UserOrder order = response.getBody();
        assertNull(order);
    }

    @Test
    public void negativeSubmitOrderTestUserNotFound() {

        // given
        when(principal.getName()).thenReturn("username123");
        when(userRepository.findByUsername("username123")).thenReturn(null);

        // when
        ResponseEntity<UserOrder> response = orderController.submit("username123", principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        UserOrder order = response.getBody();
        assertNull(order);
    }

    @Test
    public void getOrdersForUser() {

        // given
        when(principal.getName()).thenReturn("username123");
        when(userRepository.findByUsername("username123")).thenReturn(getUser());

        // when
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("username123", principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
    }

    @Test
    public void negativeGetOrdersForUserUnauthorized() {

        // given
        when(principal.getName()).thenReturn("username123");

        // when
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("username321", principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        List<UserOrder> orders = response.getBody();
        assertNull(orders);
    }

    @Test
    public void negativeGetOrdersForUserNotFound() {

        // given
        when(principal.getName()).thenReturn("username321");

        // when
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("username321", principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        List<UserOrder> orders = response.getBody();
        assertNull(orders);
    }

}
