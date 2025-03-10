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
public class TestCases_F {

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

    // Cart
    @Test
    void testAddCart() {
        Cart invalidCart = cartService.addCart(null);
        assertNull(invalidCart);
    }

    @Test
    void testGetCartsFailure() {
        //cartService.clearCarts();

        ArrayList<Cart> result = cartService.getCarts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetCartById() {
        UUID invalidId = UUID.randomUUID();
        Cart result = cartService.getCartById(invalidId);
        assertNull(result);
    }

    @Test
    void testGetCartByUserId() {
        UUID invalidUserId = UUID.randomUUID();
        Cart result = cartService.getCartByUserId(invalidUserId);
        assertNull(result);
    }

    @Test
    void testDeleteCartById() {
        UUID invalidId = UUID.randomUUID();
        cartService.deleteCartById(invalidId);
        Cart result = cartService.getCartById(invalidId);
        assertNull(result);
    }

    // Order
//    @Test
//    void testAddOrder() {
//        orderService.addOrder(null);
//    }

    @Test
    void testGetOrderById() {
        UUID invalidId = UUID.randomUUID();
        Order result = orderService.getOrderById(invalidId);
        assertNull(result);
    }

    @Test
    void testDeleteOrderById() {
        UUID invalidId = UUID.randomUUID();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.deleteOrderById(invalidId));
        assertEquals("Order not found with ID: " + invalidId, exception.getMessage());
    }

}
