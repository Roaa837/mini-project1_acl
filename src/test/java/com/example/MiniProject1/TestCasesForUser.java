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
    void testAddDuplicateUser() {
        User user = new User("John Doe", new ArrayList<>());
        userService.addUser(user);
        User duplicate = userService.addUser(user);
        assertNotNull(duplicate);
        assertEquals(user.getId(), duplicate.getId());
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
    void getUsers_Edge_MultipleUsers() {
        userService.addUser(new User("User1", new ArrayList<>()));
        userService.addUser(new User("User2", new ArrayList<>()));
        userService.addUser(new User("User3", new ArrayList<>()));
        userService.addUser(new User("User4", new ArrayList<>()));
        List<User> users = userService.getUsers();
        assertTrue(users.size() > 2, "Expected more than 2 users, but found " + users.size());
    }

    @Test
    void getUsers_Edge_NoUsers() {
        // Ensure all users are deleted
        List<User> existingUsers = userService.getUsers();
        for (User user : existingUsers) {
            userService.deleteUserById(user.getId());
        }

        List<User> usersAfterDeletion = userService.getUsers();

        assertTrue(usersAfterDeletion.isEmpty(), "Expected an empty list, but found " + usersAfterDeletion.size());
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
    void testGetOrdersByUserIdNotFound() {
        UUID nonExistentUserId = UUID.randomUUID();

        assertThrows(ResponseStatusException.class, () -> {
            userService.getOrdersByUserId(nonExistentUserId);
        });
    }


    @Test
    void getOrdersByUserId_Edge_UserHasMultipleOrders() {
        Order order1 = new Order(UUID.randomUUID(), userId, 100.0, new ArrayList<>());
        Order order2 = new Order(UUID.randomUUID(), userId, 150.0, new ArrayList<>());

        userRepository.addOrderToUser(userId, order1);
        userRepository.addOrderToUser(userId, order2);

        // Print all orders in repository to check if they were actually added
        System.out.println("Orders in repository: " + userRepository.getOrdersByUserId(userId));

        List<Order> orders = userService.getOrdersByUserId(userId);
        System.out.println("Orders retrieved from service: " + orders);

        assertTrue(orders.size() > 1, "Expected more than 1 order, but got: " + orders.size());
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
