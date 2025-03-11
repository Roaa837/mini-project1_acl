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
    public UserRepository() {
    }

    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/users.json";
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
            ArrayList<User> users = new ArrayList<>(findAll());
            return users.stream()
                    .filter(user ->user.getId()!= null &&
                            user.getId().equals(userId))
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
            ArrayList<User> users = findAll(); // Load all users

            // Find the user in the list and update the orders
            for (User user : users) {
                if (user.getId().equals(userId)) {
                    user.getOrders().add(order);
                    break; // Stop after finding the user
                }
            }

            overrideData(users); // Save the updated users list to file

            System.out.println(getUserById(userId).getOrders()); // Debugging
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to add order to users.json");
        }
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
        try {
            ArrayList<User> users = findAll();
            User user = getUserById(userId);
            boolean removed = user.getOrders().removeIf(order -> order.getId() != null &&order.getId().equals(orderId));
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
            ArrayList<User> users = findAll(); // Load all users

            // Ensure user exists before attempting to remove
            boolean userExists = users.stream().anyMatch(user -> user.getId() != null && user.getId().equals(userId));
            if (!userExists) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }

            // Remove user safely
            users.removeIf(user -> user.getId() != null && user.getId().equals(userId));

            overrideData(users); // Save updated list

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete user from users.json");
        }
    }




}


