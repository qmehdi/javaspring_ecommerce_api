package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SareetaApplicationTestsUtil {

    public static User getUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("username123");
        user.setPassword("password123");
        user.setCart(getCart(user));
        return user;
    }

    public static Cart getCart(User user) {
        Cart cart = new Cart();
        cart.setId((long) 1);
        List<Item> items = getItems();
        cart.setItems(items);
        cart.setUser(user);
        BigDecimal bigDecimal = new BigDecimal(0);
        for (Item item: items) {
            bigDecimal = bigDecimal.add(item.getPrice());
        }
        cart.setTotal(bigDecimal);
        return cart;
    }

    public static List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            items.add(getItem(i));
        }
        return items;
    }

    public static Item getItem(long id) {
        Item item = new Item();
        item.setId(id);
        item.setName("item" + id);
        item.setDescription("description123 ...");
        BigDecimal bigDecimal = BigDecimal.valueOf(item.getId() * 2);
        item.setPrice(bigDecimal);
        return item;
    }
}


