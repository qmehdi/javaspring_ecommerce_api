package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

// ####################
import static org.mockito.Mockito.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
// ####################

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class SareetaApplicationTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private CartRepository cartRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserController userController;

	@InjectMocks
	private CartController cartController;

	@InjectMocks
	private ItemController itemController;

	@InjectMocks
	private OrderController orderController;

	@Before
	public void before() {
		when(userRepository.findByUsername("username123")).thenReturn(getUser());
		when(itemRepository.findById(any())).thenReturn(Optional.of(getItem(1)));
		when(itemRepository.findById(1L)).thenReturn(Optional.of(getItem(1)));
		when(itemRepository.findAll()).thenReturn(getItems());
		when(itemRepository.findByName("item")).thenReturn(Arrays.asList(getItem(1), getItem(2)));
		when(orderRepository.findByUser(any())).thenReturn(getOrders());
		userController = new UserController();
		injectObjects("userRepository", userController, userRepository);
		injectObjects("cartRepository", userController, cartRepository);
		injectObjects("passwordEncoder", userController, passwordEncoder);
	}

	@Test
	public void addToCartTest() {
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(1);
		modifyCartRequest.setQuantity(3);
		modifyCartRequest.setUsername("username123");

		ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		Cart actualCart = response.getBody();
		Cart generatedCart = getCart(getUser());

		assertNotNull(actualCart);

		Item item = getItem(modifyCartRequest.getItemId());
		BigDecimal price = item.getPrice();
		BigDecimal expectedTotal = price.multiply(BigDecimal.valueOf(modifyCartRequest.getQuantity())).add(generatedCart.getTotal());
		assertEquals("username123", actualCart.getUser().getUsername());
		assertEquals(generatedCart.getItems().size() + modifyCartRequest.getQuantity(), actualCart.getItems().size());
		assertEquals(getItem(0), actualCart.getItems().get(0));
		assertEquals(expectedTotal, actualCart.getTotal());
		verify(cartRepository, times(1)).save(actualCart);
	}

	@Test
	public void removeFromCartTest() {
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(1);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername("username123");

		ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		Cart actualCart = response.getBody();
		Cart generatedCart = getCart(getUser());

		assertNotNull(actualCart);
		Item item = getItem(modifyCartRequest.getItemId());
		BigDecimal price = item.getPrice();
		BigDecimal expectedTotal = generatedCart.getTotal().subtract(price.multiply(BigDecimal.valueOf(modifyCartRequest.getQuantity())));

		assertEquals("username123", actualCart.getUser().getUsername());
		assertEquals(generatedCart.getItems().size() - modifyCartRequest.getQuantity(), actualCart.getItems().size());
		assertEquals(getItem(0), actualCart.getItems().get(0));
		assertEquals(expectedTotal, actualCart.getTotal());
		verify(cartRepository, times(1)).save(actualCart);
	}

	@Test
	public void negativeRemoveFromCartTest() {
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setQuantity(2);
		modifyCartRequest.setItemId(1);
		modifyCartRequest.setUsername("username789");
		ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());

		modifyCartRequest.setItemId(22);
		modifyCartRequest.setUsername("username123");
		response = cartController.removeFromcart(modifyCartRequest);
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(userRepository, times(1)).findByUsername("username123");
	}

	@Test
	public void negativeAddToCart() {
		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setQuantity(2);
		modifyCartRequest.setItemId(1);
		modifyCartRequest.setUsername("username789");
		ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());

		modifyCartRequest.setItemId(200);
		modifyCartRequest.setUsername("username123");
		response = cartController.addTocart(modifyCartRequest);
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(userRepository, times(1)).findByUsername("username123");
	}

	@Test
	public void getItemsTest() {
		ResponseEntity<List<Item>> response = itemController.getItems();
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		List<Item> items = response.getBody();
		assertEquals(getItems(), items);
		verify(itemRepository, times(1)).findAll();
	}

	@Test
	public void getItemByIdTest() {
		ResponseEntity<Item> response = itemController.getItemById(1L);
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		Item item = response.getBody();
		assertEquals(getItem(1L), item);
		verify(itemRepository, times(1)).findById(1L);
	}

	@Test
	public void negativeGetItemByIdTest() {
		ResponseEntity<Item> response = itemController.getItemById(22L);
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(itemRepository, times(1)).findById(22L);
	}

	@Test
	public void getItemByNameTest() {
		ResponseEntity<List<Item>> response = itemController.getItemsByName("item");
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		List<Item> items = Arrays.asList(getItem(0), getItem(1));
		assertEquals(getItems(), items);
		verify(itemRepository, times(1)).findByName("item");
	}

	@Test
	public void negativeGetItemByNameTest() {
		ResponseEntity<List<Item>> response = itemController.getItemsByName("invalid item");
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(itemRepository, times(1)).findByName("invalid item");
	}

	@Test
	public void submitTest() {
		ResponseEntity<UserOrder> response = orderController.submit("username123");
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		UserOrder order = response.getBody();
		assertEquals(getItems(), order.getItems());
		assertEquals(getUser().getId(), order.getUser().getId());
		verify(orderRepository, times(1)).save(order);
	}

	@Test
	public void negativeSubmitTest() {
		ResponseEntity<UserOrder> response = orderController.submit("username789");
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(userRepository, times(1)).findByUsername("username789");
	}

	@Test
	public void getOrdersForUserTest() {
		ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("username123");
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		List<UserOrder> orders = response.getBody();
		assertEquals(getOrders().size(), orders.size());
	}

	@Test
	public void negativeGetOrdersForUserTest() {
		ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("username789");
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		List<UserOrder> orders = response.getBody();
		assertNull(orders);
		verify(userRepository, times(1)).findByUsername("username789");
	}

	@Test
	public void createUserTest() {
		when(passwordEncoder.encode("password123")).thenReturn("hash123");
		CreateUserRequest request = new CreateUserRequest();
		request.setUsername("username234");
		request.setPassword("password123");
		request.setVerifyPassword("password123");
		ResponseEntity<?> response = userController.createUser(request);
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		User user = (User) response.getBody();
		Assert.assertNotNull(user);
		assertEquals(0, user.getId());
		assertEquals("username234", user.getUsername());
		assertEquals("hash123", user.getPassword());
	}

	@Test
	public void negativeCreateUserTest() {
		CreateUserRequest request = new CreateUserRequest();
		request.setUsername("username234");
		request.setPassword("pass");
		request.setVerifyPassword("pass");
		ResponseEntity<?> response = userController.createUser(request);
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		request.setPassword("password123");
		request.setVerifyPassword("password223");
		response = userController.createUser(request);
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void findByIdTest() {
		User user = getUser();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		ResponseEntity<User> response = userController.findById(1L);
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		User actualUser = response.getBody();
		Assert.assertNotNull(actualUser);
		assertEquals(1L, actualUser.getId());
		assertEquals("username123", actualUser.getUsername());
		assertEquals("password123", actualUser.getPassword());
	}

	@Test
	public void negativeFindByIdTest() {
		ResponseEntity<User> response = userController.findById(23L);
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assert.assertNull(response.getBody());
	}

	@Test
	public void findByUsernameTest() {
		User user = getUser();
		when(userRepository.findByUsername("username123")).thenReturn(user);
		ResponseEntity<User> response = userController.findByUserName("username123");
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		User actualUser = response.getBody();
		Assert.assertNotNull(actualUser);
		assertEquals(1L, actualUser.getId());
		assertEquals("username123", actualUser.getUsername());
		assertEquals("password123", actualUser.getPassword());
	}

	@Test
	public void negativeFindByUsernameTest() {
		ResponseEntity<User> response = userController.findByUserName("username");
		Assert.assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		User user = response.getBody();
		Assert.assertNull(user);
	}


	private void injectObjects(String fieldName, Object target, Object inject) {
		try {
			boolean isPrivate = false;
			Field field = target.getClass().getDeclaredField(fieldName);
			if (!field.isAccessible()) {
				isPrivate = true;
				field.setAccessible(true);
			}
			field.set(target, inject);
			if (isPrivate) {
				field.setAccessible(false);
			}
		} catch (NoSuchFieldException | IllegalAccessException exception) {
			System.out.println(exception.getMessage());
		}
	}

	private List<UserOrder> getOrders() {
		List<UserOrder> userOrders = new ArrayList<>();
		for(int i = 0; i < 2; i++) {
			UserOrder userOrder = new UserOrder();
			User user = getUser();
			Cart cart = getCart(user);
			userOrder.setId((long) (i + 1));
			userOrder.setItems(cart.getItems());
			userOrder.setTotal(cart.getTotal());
			userOrder.setUser(user);
			userOrders.add(userOrder);
		}
		return userOrders;
	}

	private Item getItem(long id) {
		Item item = new Item();
		item.setId(id);
		item.setName("Item " + id);
		item.setDescription("Item description ...");
		BigDecimal price = BigDecimal.valueOf(item.getId() * 2);
		item.setPrice(price);
		return item;
	}

	private List<Item> getItems() {
		List<Item> items = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			items.add(getItem(i));
		}
		return items;
	}

	private Cart getCart(User user) {
		Cart cart = new Cart();
		cart.setId(1L);
		List<Item> items = getItems();
		cart.setItems(items);
		BigDecimal total = new BigDecimal(0);
		for (Item item: items) {
			total = total.add(item.getPrice());
		}
		cart.setTotal(total);
		cart.setUser(user);
		return cart;
	}

	private User getUser() {
		User user = new User();
		user.setId(1L);
		user.setUsername("username123");
		user.setPassword("password123");
		Cart cart = getCart(user);
		user.setCart(cart);
		return user;
	}

}
