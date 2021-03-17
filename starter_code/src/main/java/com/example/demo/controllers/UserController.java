package com.example.demo.controllers;

import java.security.Principal;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final Logger log = LogManager.getLogger(this.getClass());

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id, Principal principal) {

		Optional<User> optionalUser = userRepository.findById(id);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			if(user.getId() == id) {
				if(!user.getUsername().equals(principal.getName())) {
					log.info("[Success] [Find User] Found user by ID: " + id);
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				}
				log.error("[Fail] [Find User] Failed to find user by ID: " + id);
				return ResponseEntity.ok(user);
			}
		}
		return ResponseEntity.notFound().build();
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username, Principal principal) {
		if (!username.equals(principal.getName())) {
			log.error("[Fail] [Find User] Failed to find user by Username: " + username + " Reason: Unauthorized");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		User user = userRepository.findByUsername(username);

		if (user == null) {
			log.error("[Fail] [Find User] Failed to find user by username: " + username);
			return ResponseEntity.notFound().build();
		}
		else {
			log.info("[Success] [Find User] Found user by username: " + username);
			return ResponseEntity.ok(user);
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = userRepository.findByUsername(createUserRequest.getUsername());
		if (user == null) {
			user = new User();
			if (createUserRequest.getPassword().length() < 8 || createUserRequest.getPassword().length() > 32) {
				log.error("[Fail] [Create User] Password length was not in between (8-32)");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password length should be between 8 to 32");
			}
			if (!createUserRequest.getPassword().equals(createUserRequest.getVerifyPassword())) {
				log.error("[Fail] [Create User] Passwords does not match");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords does not match");
			}
			user.setUsername(createUserRequest.getUsername());
			user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			userRepository.save(user);
			log.info("[Success] [Create User] User created by username: " + createUserRequest.getUsername());
			return ResponseEntity.ok(user);
		} else {
			log.error("[Fail] [Create User] Failed to create user by username: '" + createUserRequest.getUsername() + "' Reason: User already exist.");
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
}
