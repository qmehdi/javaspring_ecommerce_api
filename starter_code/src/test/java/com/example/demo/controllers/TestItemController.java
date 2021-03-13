package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.demo.SareetaApplicationTestsUtil.getItems;

import static com.example.demo.SareetaApplicationTestsUtil.getItem;
import static com.example.demo.SareetaApplicationTestsUtil.getItems;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class TestItemController {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    public TestItemController() {
    }

    @Test
    public void getItemsTest() {

        // given
        when(itemRepository.findAll()).thenReturn(getItems());

        // when
        ResponseEntity<List<Item>> response = itemController.getItems();

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Item> items = response.getBody();
        assertNotNull(items);
    }

    @Test
    public void negativeGetItemsTestEmpty() {

        // given
        when(itemRepository.findAll()).thenReturn(new ArrayList<Item>());

        // when
        ResponseEntity<List<Item>> response = itemController.getItems();

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        List<Item> items = response.getBody();
        assertNull(items);
    }

    @Test
    public void getItemByIdTest() {

        // given
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(getItem(1)));

        // when
        ResponseEntity<Item> response = itemController.getItemById(1L);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Item item = response.getBody();
        assertNotNull(item);
        assertEquals(new Long(1), item.getId());
        assertEquals(item.getName(), "item1");
        assertEquals(item.getDescription(), "description123 ...");
    }

    @Test
    public void negativeGetItemByIdTestItemNotFound() {

        // given
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(getItem(4L)));

        // when
        ResponseEntity<Item> response = itemController.getItemById(1L);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Item item = response.getBody();
        assertNull(item);
    }

    @Test
    public void getItemsByNameTest() {

        // given
        when(itemRepository.findByName("Item1")).thenReturn(Collections.singletonList(getItem(1L)));

        // when
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Item1");

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Item> items = response.getBody();
        assertNotNull(items);
    }

    @Test
    public void negativeGetItemsByNameTestItemNotFound() {

        // given
        when(itemRepository.findByName("Item1")).thenReturn(new ArrayList<>());

        // when
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Item1");

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        List<Item> items = response.getBody();
        assertNull(items);
    }

}
