package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.security.Principal;

import static com.example.demo.SareetaApplicationTestsUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestCartController {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private Principal principal;

    @Test
    public void addToCartTest() {

        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setUsername("username123");
        modifyCartRequest.setQuantity(3);
        when(userRepository.findByUsername("username123")).thenReturn(getUser());
        when(principal.getName()).thenReturn("username123");
        when(itemRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(getItem(1)));

        // when
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest, principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart actualCart = response.getBody();
        assertNotNull(actualCart);
        Cart cart = getCart(getUser());
        Item item = getItem(modifyCartRequest.getItemId());
        BigDecimal bigDecimal = item.getPrice();
        BigDecimal expectedPrice = bigDecimal.multiply(BigDecimal.valueOf(modifyCartRequest.getQuantity())).add(cart.getTotal());
        assertEquals(expectedPrice, actualCart.getTotal());
        assertEquals("username123", actualCart.getUser().getUsername());
        verify(cartRepository, times(1)).save(actualCart);
    }

    @Test
    public void removeFromCartTest() {

        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("username123");
        when(userRepository.findByUsername("username123")).thenReturn(getUser());
        when(principal.getName()).thenReturn("username123");
        when(itemRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(getItem(1)));


        // when
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest, principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart actualCart = response.getBody();
        assertNotNull(actualCart);

        Cart cart = getCart(getUser());
        Item item = getItem(cart.getId());
        BigDecimal price = item.getPrice();
        BigDecimal expectedPrice = cart.getTotal().subtract(price.multiply(BigDecimal.valueOf(modifyCartRequest.getQuantity())));
        assertEquals(expectedPrice, actualCart.getTotal());
        assertEquals("username123", actualCart.getUser().getUsername());
        assertEquals(getItem(0), actualCart.getItems().get(0));

        verify(cartRepository, times(1)).save(actualCart);
    }

    @Test
    public void negativeAddToCartTestUserUnauthorized() {

        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setUsername("username321");
        modifyCartRequest.setQuantity(3);
        when(principal.getName()).thenReturn("username123");

        // when
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest, principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void negativeAddToCartTestUserNotFound() {

        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setUsername("username321");
        modifyCartRequest.setQuantity(3);
        when(principal.getName()).thenReturn("username321");

        // when
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest, principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void negativeAddToCartTestItemNotFound() {

        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(100);
        modifyCartRequest.setQuantity(3);
        modifyCartRequest.setUsername("username123");
        when(principal.getName()).thenReturn("username123");
        when(userRepository.findByUsername("username123")).thenReturn(getUser());
        when(itemRepository.findById(100L)).thenReturn(java.util.Optional.of(getItem(200)));


        // when
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest, principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void negativeRemoveFromCartTestUserUnauthorized() {

        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setUsername("username321");
        modifyCartRequest.setQuantity(2);
        when(principal.getName()).thenReturn("username123");

        // when
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest, principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void negativeRemoveFromCartTestUserNotFound() {

        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setUsername("username321");
        modifyCartRequest.setQuantity(2);
        when(principal.getName()).thenReturn("username321");

        // when
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest, principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void negativeRemoveFromCartTestItemNotFound() {

        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(100);
        modifyCartRequest.setUsername("username123");
        modifyCartRequest.setQuantity(2);
        when(principal.getName()).thenReturn("username123");
        when(userRepository.findByUsername("username123")).thenReturn(getUser());
        when(itemRepository.findById(100L)).thenReturn(java.util.Optional.of(getItem(200)));


        // when
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest, principal);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
