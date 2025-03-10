package com.example.MiniProject1;

import static org.junit.jupiter.api.Assertions.*;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.service.CartService;
import com.example.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@SpringBootTest
public class TestCases_S {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    private Cart cart;
    private UUID cartId;
    private UUID userId;

    private Order order;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        cartId = UUID.randomUUID();
        userId = UUID.randomUUID();
        cart = new Cart(cartId, userId, new ArrayList<>());
        orderId = UUID.randomUUID();
        order = new Order(orderId, userId, 250.0, new ArrayList<>());
    }

    // cart
    @Test
    void testAddCart() {
        Cart result = cartService.addCart(cart);
        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
    }

    @Test
    void testGetCarts() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Cart> cartsInJsonFile = new ArrayList<>();
        try {
            File file = new File("src/main/java/com/example/data/carts.json");
            if (file.exists()) {
                Cart[] cartsArray = objectMapper.readValue(file, Cart[].class);
                cartsInJsonFile = new ArrayList<>(Arrays.asList(cartsArray));
            }
        } catch (Exception e) {
            fail("Failed to load carts from JSON file: " + e.getMessage());
        }

        ArrayList<Cart> result = cartService.getCarts();
        assertNotNull(result);
        assertEquals(result.size(), cartsInJsonFile.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getId(), cartsInJsonFile.get(i).getId());
        }
    }

    @Test
    void testGetCartById() {
        cartService.addCart(cart);
        Cart result = cartService.getCartById(cart.getId());
        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
    }

    @Test
    void testGetCartByUserId() {
        cartService.addCart(cart);
        Cart result = cartService.getCartByUserId(cart.getUserId());
        assertNotNull(result);
        assertEquals(cart.getUserId(), result.getUserId());
        assertEquals(cart.getId(), result.getId());
    }

//    @Test
//    void testAddProductToCart() {
//    }
//
//    @Test
//    void testDeleteProductFromCart() {
//    }

    @Test
    void testDeleteCartById() {
        cartService.addCart(cart);
        Cart theCart = cartService.getCartById(cart.getId());
        assertNotNull(theCart);
        cartService.deleteCartById(cart.getId());
        theCart = cartService.getCartById(cart.getId());
        assertNull(theCart);
    }

    // order
    @Test
    void testAddOrder() {
        orderService.addOrder(order);

        Order result = new Order();
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Order> ordersInJsonFile = new ArrayList<>();
        try {
            File file = new File("src/main/java/com/example/data/orders.json");
            if (file.exists()) {
                Order[] ordersArray = objectMapper.readValue(file, Order[].class);
                ordersInJsonFile = new ArrayList<>(Arrays.asList(ordersArray));

                for (Order o : ordersInJsonFile) {
                    if (o.getId().equals(order.getId())) {
                        result = order;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            fail("Failed to load orders from JSON file: " + e.getMessage());
        }

        assertEquals(result.getId(), order.getId());
    }

    @Test
    void testGetOrders() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Order> orderInJsonFile = new ArrayList<>();
        try {
            File file = new File("src/main/java/com/example/data/orders.json");
            if (file.exists()) {
                Order[] ordersArray = objectMapper.readValue(file, Order[].class);
                orderInJsonFile = new ArrayList<>(Arrays.asList(ordersArray));
            }
        } catch (Exception e) {
            fail("Failed to load orders from JSON file: " + e.getMessage());
        }

        ArrayList<Order> result = orderService.getOrders();
        assertNotNull(result);
        assertEquals(result.size(), orderInJsonFile.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).getId(), orderInJsonFile.get(i).getId());
        }
    }

    @Test
    void testGetOrderById() {
        orderService.addOrder(order);
        Order result = orderService.getOrderById(order.getId());
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
    }

    @Test
    void testDeleteOrderById() {
        orderService.addOrder(order);
        Order theOrder = orderService.getOrderById(order.getId());
        assertNotNull(theOrder);
        orderService.deleteOrderById(order.getId());
        theOrder = orderService.getOrderById(order.getId());
        assertNull(theOrder);
    }
}
