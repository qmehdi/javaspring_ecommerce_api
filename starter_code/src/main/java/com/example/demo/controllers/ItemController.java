package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {

		List<Item> items = itemRepository.findAll();
		if (items.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		else {
			return ResponseEntity.ok(items);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		Optional<Item> optionalItem = itemRepository.findById(id);
		if (optionalItem.isPresent()) {
			Item item = optionalItem.get();
			if (item.getId().equals(id)) {
				return ResponseEntity.ok(item);
			}
		}
		return ResponseEntity.notFound().build();
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		List<Item> items = itemRepository.findByName(name);
		if (items == null || items.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		else {
			return ResponseEntity.ok(items);
		}
	}
	
}
