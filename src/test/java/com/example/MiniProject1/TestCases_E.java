package com.example.MiniProject1;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.service.CartService;
import com.example.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestCases_E {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {

    }

    // cart
    @Test
    void addCart() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        Cart result = cartService.addCart(cart);
        assertNotNull(result);
        assertEquals(0, result.getProducts().size());
    }

    @Test
    void getCarts() {
        cartRepository.overrideData(new ArrayList<>());

        ArrayList<Cart> result = cartService.getCarts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void getCartById() {
        UUID UnAvailableId = UUID.randomUUID();

        Cart result = cartService.getCartById(UnAvailableId);

        assertNull(result);
    }

    @Test
    void getCartByUserId() {
        UUID userId = UUID.randomUUID();
        Cart cart1 = new Cart(UUID.randomUUID(), userId, new ArrayList<>());
        Cart cart2 = new Cart(UUID.randomUUID(), userId, new ArrayList<>());
        cartService.addCart(cart1);
        cartService.addCart(cart2);

        Cart result = cartService.getCartByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }

    @Test
    void addProductToCart() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, UUID.randomUUID(), new ArrayList<>());
        cartService.addCart(cart);

        Product product = new Product(UUID.randomUUID(), "v-cola", 15.0);
        cartService.addProductToCart(cartId, product);
        cartService.addProductToCart(cartId, product);

        Cart result = cartService.getCartById(cartId);
        assertNotNull(result);
        assertEquals(2, result.getProducts().size());
    }

    @Test
    void deleteProductFromCart() {
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, UUID.randomUUID(), new ArrayList<>());
        cartService.addCart(cart);

        Product product = new Product(UUID.randomUUID(), "I-Phone 15 pro", 75000.0);

        cartService.deleteProductFromCart(cartId, product);

        Cart result = cartService.getCartById(cartId);
        assertNotNull(result);
        assertEquals(0, result.getProducts().size());
    }

    @Test
    void deleteCartById() {
        UUID cartId = UUID.randomUUID();

        cartService.deleteCartById(cartId);

        Cart result = cartService.getCartById(cartId);
        assertNull(result);
    }

    // order
    @Test
    void addOrder() {
        orderRepository.overrideData(new ArrayList<>());
        Order emptyOrder = new Order();

        orderService.addOrder(emptyOrder);

        ArrayList<Order> orders = orderService.getOrders();
        assertTrue(orders.size() == 1);
    }

    @Test
    void getOrders() {
        orderRepository.overrideData(new ArrayList<>());

        ArrayList<Order> orders = orderService.getOrders();

        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    void getOrderById() {
        UUID UnAvailableId = UUID.randomUUID();

        Order order = orderService.getOrderById(UnAvailableId);

        assertNull(order);
    }

    @Test
    void deleteOrderById() {
        UUID UnAvailableId = UUID.randomUUID();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.deleteOrderById(UnAvailableId);
        });
        assertEquals("Order not found with ID: " + UnAvailableId, exception.getMessage());
    }
}
