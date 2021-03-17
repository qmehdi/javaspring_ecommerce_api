package com.example.demo.controllers;

import java.security.Principal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	private final Logger log = LogManager.getLogger(this.getClass());


	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username, Principal principal) {
		if (!username.equals(principal.getName())) {
			log.error("[Fail] [Submit Order] Unauthorized user wanted to submit order of '" + username + "'");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("[Fail] [Submit Order] User with username: '" + username + "' was not found.");
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		log.info("[Success] [Submit Order] User '" + username + "' order is submitted.");
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username, Principal principal) {
		if (!username.equals(principal.getName())) {
			log.error("[Fail] [Order History] Unauthorized user wanted to check order history of '" + username + "'");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("[Fail] [Order History] User with username: '" + username + "' was not found.");
			return ResponseEntity.notFound().build();
		}
		List<UserOrder> orders = orderRepository.findByUser(user);
		log.info("[Success] [Order History] Retrieved order history for user " + username);
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
