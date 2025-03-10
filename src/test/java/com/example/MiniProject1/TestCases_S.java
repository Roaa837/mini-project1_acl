package com.example.MiniProject1;

import static org.junit.jupiter.api.Assertions.*;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
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

    @Test
    void testAddProductToCart() {
        cartService.addCart(cart);
        int size = cart.getProducts().size();
        cartService.addProductToCart(cart.getId(),product);
        Cart newCart = cartService.getCartById(cart.getId());
        int newSize = newCart.getProducts().size();
        assertEquals(size + 1, newSize);
    }

    @Test
    void testDeleteProductFromCart() {
        cartService.addCart(cart);
        cartService.addProductToCart(cart.getId(),product);
        Cart oldCart = cartService.getCartById(cart.getId());
        int oldSize = oldCart.getProducts().size();
        cartService.deleteProductFromCart(cart.getId(),product);
        Cart newCart = cartService.getCartById(cart.getId());
        int newSize = newCart.getProducts().size();
        assertEquals(oldSize, newSize + 1);
    }

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
        Order theOrder = orderService.getOrderById(order.getId());
        assertNull(theOrder);
        orderService.addOrder(order);
        theOrder = orderService.getOrderById(order.getId());
        assertNotNull(theOrder);
        assertEquals(theOrder.getId(), order.getId());
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
