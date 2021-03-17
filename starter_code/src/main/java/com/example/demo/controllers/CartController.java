package com.example.demo.controllers;

import java.security.Principal;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;

	private final Logger log = LogManager.getLogger(this.getClass());
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request, Principal principal) {
		if(!request.getUsername().equals(principal.getName())) {
			log.error("[Fail] [Remove from Cart] Unauthorized user wanted to add item to cart of '" + request.getUsername() + "'");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.error("[Fail] [Add to Cart] User with username: '" + request.getUsername() + "' was not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(item.isPresent()) {
			Item temp = item.get();
			if (!temp.getId().equals(request.getItemId())) {
				log.error("[Fail] [Add to Cart] Item with Item Id: " + request.getItemId() + " was not fond.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		log.info("[Success] [Add to Cart] Item was added to cart for user " + request.getUsername());
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request, Principal principal) {
		if (!request.getUsername().equals(principal.getName())) {
			log.error("[Fail] [Remove from Cart] Unauthorized user wanted to remove item from cart of '" + request.getUsername() + "'");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.error("[Fail] [Remove from Cart] User with username: '" + request.getUsername() + "' was not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(item.isPresent()) {
			Item temp = item.get();
			if (!temp.getId().equals(request.getItemId())) {
				log.error("[Fail] [Remove from Cart] Item with Item Id: " + request.getItemId() + " was not fond.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);
		log.info("[Success] [Remove from Cart] Item was removed from cart for user " + request.getUsername());
		return ResponseEntity.ok(cart);
	}
		
}
