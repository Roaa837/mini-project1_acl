package com.example.MiniProject1;

import static org.junit.jupiter.api.Assertions.*;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
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
    private CartRepository cartRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private Cart cart;
    private UUID cartId;

    private UUID userId;

    private Order order;
    private UUID orderId;

    private Product product;
    private UUID productId;

    @BeforeEach
    void setUp() {
        cartId = UUID.randomUUID();
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        productId = UUID.randomUUID();

        cart = new Cart(cartId, userId, new ArrayList<>());
        order = new Order(orderId, userId, 250.0, new ArrayList<>());
        product = new Product(productId, "v-cola", 21.50);
    }

    // Cart
    @Test
    void testAddCart() {
        Cart invalidCart = cartService.addCart(null);
        assertNull(invalidCart);
    }

    @Test
    void testGetCarts() {
        cartRepository.overrideData(new ArrayList<>());
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
    void testAddProductToCart() {
        cartService.addCart(cart);
        int size = cart.getProducts().size();
        cartService.addProductToCart(cart.getId(),null);
        Cart newCart = cartService.getCartById(cart.getId());
        int newSize = newCart.getProducts().size();
        assertEquals(size, newSize);
    }

    @Test
    void testDeleteProductFromCart() {
        cartService.addCart(cart);
        int oldSize = cart.getProducts().size();
        cartService.deleteProductFromCart(cart.getId(),new Product(UUID.randomUUID(), "v-cola", 21.50));
        Cart newCart = cartService.getCartById(cart.getId());
        int newSize = newCart.getProducts().size();
        assertEquals(oldSize, newSize);
    }

    @Test
    void testDeleteCartById() {
        ArrayList<Cart> cartsBeforeDelete = cartService.getCarts();
        int sizeBeforeDelete = cartsBeforeDelete.size();

        UUID invalidId = UUID.randomUUID();
        cartService.deleteCartById(invalidId);

        ArrayList<Cart> cartsAfterDelete = cartService.getCarts();
        int sizeAfterDelete = cartsAfterDelete.size();

        assertEquals(sizeBeforeDelete, sizeAfterDelete);
    }

//    Order
    @Test
    void testAddOrder() {
        ArrayList<Order> ordersBeforeAdd = orderService.getOrders();
        int sizeBeforeAdd = ordersBeforeAdd.size();

        orderService.addOrder(null);

        ArrayList<Order> ordersAfterAdd = orderService.getOrders();
        int sizeAfterAdd = ordersAfterAdd.size();

        assertEquals(sizeBeforeAdd, sizeAfterAdd);
    }

    @Test
    void testGetOrders() {
        orderRepository.overrideData(new ArrayList<>());
        ArrayList<Order> result = orderService.getOrders();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

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
