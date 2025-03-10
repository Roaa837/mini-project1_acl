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

    @BeforeEach
    void setUp() {

    }

    @Test
    void addCart() {
        Cart cart = new Cart(UUID.randomUUID(), UUID.randomUUID(), new ArrayList<>());
        Cart result = cartService.addCart(cart);
        assertNotNull(result);
        assertEquals(0, result.getProducts().size());
    }

    @Test
    void getCarts_ShouldReturnEmptyList_WhenNoCartsExist() {
        CartService cartService = new CartService(new CartRepository()); // Fresh repository with no carts

        // Act
        ArrayList<Cart> result = cartService.getCarts();

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertTrue(result.isEmpty(), "The list should be empty when no carts exist.");
    }


    @Test
    void getCartById_NonExistentId() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Cart result = cartService.getCartById(nonExistentId);

        // Assert
        assertNull(result);
    }

    @Test
    void getCartByUserId_MultipleCartsSameUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Cart cart1 = new Cart(UUID.randomUUID(), userId, new ArrayList<>());
        Cart cart2 = new Cart(UUID.randomUUID(), userId, new ArrayList<>());
        cartService.addCart(cart1);
        cartService.addCart(cart2);

        // Act
        Cart result = cartService.getCartByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId()); // Ensures at least one is returned
    }

    @Test
    void addProductToCart_DuplicateProduct() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, UUID.randomUUID(), new ArrayList<>());
        cartService.addCart(cart);
        Product product = new Product(UUID.randomUUID(), "Laptop", 1500.0);
        cartService.addProductToCart(cartId, product);

        // Act
        cartService.addProductToCart(cartId, product);

        // Assert
        Cart result = cartService.getCartById(cartId);
        assertNotNull(result);
        assertEquals(2, result.getProducts().size()); // Checks if duplicate products exist
    }

    @Test
    void deleteProductFromCart_RemoveNonExistentProduct() {
        // Arrange
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, UUID.randomUUID(), new ArrayList<>());
        cartService.addCart(cart);
        Product product = new Product(UUID.randomUUID(), "Phone", 700.0);

        // Act
        cartService.deleteProductFromCart(cartId, product);

        // Assert
        Cart result = cartService.getCartById(cartId);
        assertNotNull(result);
        assertEquals(0, result.getProducts().size());
    }

    @Test
    void deleteCartById_EmptyRepository() {
        // Arrange
        UUID cartId = UUID.randomUUID();

        // Act
        cartService.deleteCartById(cartId);

        // Assert
        Cart result = cartService.getCartById(cartId);
        assertNull(result);
    }

    //order
    @Test
    void addOrder_shouldHandleEmptyOrder() {
        // Arrange
        Order emptyOrder = new Order();

        // Act
        orderService.addOrder(emptyOrder);
        ArrayList<Order> orders = orderService.getOrders();

        // Assert
        assertTrue(orders.contains(emptyOrder), "Empty order should still be added");
    }

    @Test
    void getOrders_shouldReturnEmptyListWhenNoOrdersExist() {
        // Arrange
        orderRepository.overrideData(new ArrayList<>()); // Ensure repository is empty

        // Act
        ArrayList<Order> orders = orderService.getOrders();

        // Assert
        assertTrue(orders.isEmpty(), "Should return an empty list if no orders exist");
    }

    @Test
    void getOrderById_shouldReturnNullForNonexistentOrder() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Order order = orderService.getOrderById(nonExistentId);

        // Assert
        assertNull(order, "Should return null when order ID does not exist");
    }

    @Test
    void deleteOrderById_shouldThrowExceptionWhenOrderDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.deleteOrderById(nonExistentId);
        });
        assertEquals("Order not found with ID: " + nonExistentId, exception.getMessage());
    }
}
