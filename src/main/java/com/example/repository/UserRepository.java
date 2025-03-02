package com.example.repository;

import com.example.model.Order;
import com.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import com.example.model.User;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@SuppressWarnings("rawtypes")
@Repository
public class UserRepository extends MainRepository<User> {
    private final Order order;
    private final UserService userService;
    private final User user;

    public UserRepository(Order order, UserService userService, User user) {
        this.order = order;
        this.userService = userService;
        this.user = user;
    }

    @Override
    protected String getDataPath() {
        return "src/main/java/com.example/data/users.json";
    }
    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }
    public ArrayList<User> getUsers() {
        try {
            return new ArrayList<>(findAll());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve users from users.json");
        }
    }
    public User getUserById(UUID userId) {
        try {
            // Step 1: Retrieve all users from JSON
            List<User> users = findAll();

            // Step 2: Find the user by ID
            return users.stream()
                    .filter(user -> user.getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve user from users.json");
        }
    }

    public List<Order> getOrdersByUserId(UUID userId)
    {
        try {
            return getUserById(userId).getOrders();
        }
        catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve orders from the user");
        }

    }
    public User addUser(User user) {
        try {

            ArrayList<User> users = findAll();
            if (user.getId() == null) {
                user.setId(UUID.randomUUID());
            }

            users.add(user);
            overrideData(users);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to add user to users.json");
        }
    }
    public void addOrderToUser(UUID userId, Order order) {
        try {

            ArrayList<User> users = findAll();
            User user = getUserById(userId);
            user.getOrders().add(order);
            overrideData(users);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to add order to users.json");
        }
    }
    public void removeOrderFromUser(UUID userId, UUID orderId) {
        try {
            ArrayList<User> users = findAll();
            User user = getUserById(userId);
            boolean removed = user.getOrders().removeIf(order -> order.getId().equals(orderId));
            if (!removed) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for this user");
            }

            // Step 5: Save the updated users list back to JSON
            overrideData(users);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to remove order from users.json");
        }
    }
    public void deleteUserById(UUID userId) {
        try {
            ArrayList<User> users = findAll();
            boolean removed = users.removeIf(user -> user.getId().equals(userId));
            if (!removed) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }

            overrideData(users);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete user from users.json");
        }
    }



}


