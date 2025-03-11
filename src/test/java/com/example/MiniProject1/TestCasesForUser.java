package com.example.MiniProject1;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestCasesForUser {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        testUser = new User(UUID.randomUUID(), "Test User", new ArrayList<>());
        userRepository.addUser(testUser);
        userId = testUser.getId();
    }

    @Test
    void addUser_Success() {
        User user = new User("John Doe", new ArrayList<>());

        User savedUser = userService.addUser(user);

        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
    }

    @Test
    void addUser_Fail_NullUser() {
        assertThrows(NullPointerException.class, () -> userService.addUser(null));
    }

    @Test
    void addUser_Edge_EmptyName() {
        User user = new User("", new ArrayList<>());

        User savedUser = userService.addUser(user);

        assertEquals("", savedUser.getName());
    }

    @Test
    void getUsers_Success() {
        userService.addUser(new User("Alice", new ArrayList<>()));

        List<User> users = userService.getUsers();

        assertFalse(users.isEmpty());
    }

    @Test
    void getUsers_Fail_NoUsers() {
        List<User> users = userService.getUsers();

        assertTrue(users.isEmpty());
    }

    @Test
    void getUsers_Edge_MultipleUsers() {
        userService.addUser(new User("User1", new ArrayList<>()));
        userService.addUser(new User("User2", new ArrayList<>()));

        List<User> users = userService.getUsers();

        assertEquals(2, users.size());
    }

    @Test
    void getUserById_Success() {
        User user = userService.addUser(new User("Bob", new ArrayList<>()));

        User foundUser = userService.getUserById(user.getId());

        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void getUserById_Fail_NotFound() {
        UUID randomId = UUID.randomUUID();

        assertThrows(ResponseStatusException.class, () -> userService.getUserById(randomId));
    }

    @Test
    void getUserById_Edge_EmptyUUID() {
        UUID emptyUUID = new UUID(0, 0);

        assertThrows(ResponseStatusException.class, () -> userService.getUserById(emptyUUID));
    }

    @Test
    void deleteUserById_Success() {
        User user = userService.addUser(new User("Charlie", new ArrayList<>()));

        userService.deleteUserById(user.getId());

        assertThrows(ResponseStatusException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    void deleteUserById_Fail_NotFound() {
        UUID randomId = UUID.randomUUID();

        assertThrows(ResponseStatusException.class, () -> userService.deleteUserById(randomId));
    }

    @Test
    void deleteUserById_Edge_DeleteTwice() {
        User user = userService.addUser(new User("David", new ArrayList<>()));
        userService.deleteUserById(user.getId());

        assertThrows(ResponseStatusException.class, () -> userService.deleteUserById(user.getId()));
    }

    @Test
    void getOrdersByUserId_Success() {
        List<Order> orders = userService.getOrdersByUserId(userId);
        assertNotNull(orders);
        assertEquals(0, orders.size());
    }

    @Test
    void getOrdersByUserId_Fail_UserNotFound() {
        UUID nonExistentUserId = UUID.randomUUID();
        List<Order> orders = userService.getOrdersByUserId(nonExistentUserId);
        assertNotNull(orders);
        assertEquals(0, orders.size());
    }

    @Test
    void getOrdersByUserId_Edge_UserHasMultipleOrders() {
        Order order1 = new Order(UUID.randomUUID(), userId, 100.0, new ArrayList<>());
        Order order2 = new Order(UUID.randomUUID(), userId, 150.0, new ArrayList<>());
        userRepository.addOrderToUser(userId, order1);
        userRepository.addOrderToUser(userId, order2);
        List<Order> orders = userService.getOrdersByUserId(userId);
        assertEquals(2, orders.size());
    }

    @Test
    void addOrderToUser_Success() {
        Cart cart = new Cart(userId, new ArrayList<>(List.of(new Product("Product1", 50.0))));
        cartRepository.addCart(cart);
        userService.addOrderToUser(userId);
        assertEquals(1, userService.getOrdersByUserId(userId).size());
    }

    @Test
    void addOrderToUser_Fail_NoCart() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.addOrderToUser(userId));
        assertEquals("404 NOT_FOUND \"Cart not found\"", exception.getMessage());
    }

    @Test
    void addOrderToUser_Edge_EmptyCart() {
        Cart cart = new Cart(userId, new ArrayList<>());
        cartRepository.addCart(cart);
        userService.addOrderToUser(userId);
        List<Order> orders = userService.getOrdersByUserId(userId);
        assertEquals(1, orders.size());
        assertEquals(0, orders.get(0).getProducts().size());
    }

    @Test
    void emptyCart_Success() {
        Cart cart = new Cart(userId, new ArrayList<>(List.of(new Product("Product1", 50.0))));
        cartRepository.addCart(cart);
        userService.emptyCart(userId);
        assertEquals(0, cartRepository.getCartByUserId(userId).getProducts().size());
    }

    @Test
    void emptyCart_Fail_NoCart() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.emptyCart(userId));
        assertEquals("404 NOT_FOUND \"Cart not found for this user\"", exception.getMessage());
    }

    @Test
    void emptyCart_Edge_CartAlreadyEmpty() {
        Cart cart = new Cart(userId, new ArrayList<>());
        cartRepository.addCart(cart);
        userService.emptyCart(userId);
        assertEquals(0, cartRepository.getCartByUserId(userId).getProducts().size());
    }

    @Test
    void removeOrderFromUser_Success() {
        Order order = new Order(UUID.randomUUID(), userId, 100.0, new ArrayList<>());
        userRepository.addOrderToUser(userId, order);
        userService.removeOrderFromUser(userId, order.getId());
        assertEquals(0, userService.getOrdersByUserId(userId).size());
    }

    @Test
    void removeOrderFromUser_Fail_OrderNotFound() {
        UUID nonExistentOrderId = UUID.randomUUID();
        userService.removeOrderFromUser(userId, nonExistentOrderId);
        assertEquals(0, userService.getOrdersByUserId(userId).size());
    }

    @Test
    void removeOrderFromUser_Edge_UserHasMultipleOrders() {
        Order order1 = new Order(UUID.randomUUID(), userId, 100.0, new ArrayList<>());
        Order order2 = new Order(UUID.randomUUID(), userId, 150.0, new ArrayList<>());
        userRepository.addOrderToUser(userId, order1);
        userRepository.addOrderToUser(userId, order2);
        userService.removeOrderFromUser(userId, order1.getId());
        List<Order> orders = userService.getOrdersByUserId(userId);
        assertEquals(1, orders.size());
        assertEquals(order2.getId(), orders.get(0).getId());
    }
}
