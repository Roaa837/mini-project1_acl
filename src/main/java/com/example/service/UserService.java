package com.example.service;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("rawtypes")
@Service

public class UserService extends MainService<User> {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;




    public UserService(UserRepository userRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;

    }
    public User addUser(User user) {
        return userRepository.addUser(user);
    }
    public ArrayList<User> getUsers() {
        return new ArrayList<>(userRepository.getUsers());
    }
    public User getUserById(UUID userId)
    {
        return userRepository.getUserById(userId);
    }
    public List<Order> getOrdersByUserId(UUID userId)
    {
        return userRepository.getOrdersByUserId(userId);
    }
    public void deleteUserById(UUID userId)
    {
        userRepository.deleteUserById(userId);
    }

    public void addOrderToUser(UUID userId)
    {
       Cart cart =  cartRepository.getCartByUserId(userId);
       if (cart == null){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
       }
       double total_price = cart.getProducts().stream().mapToDouble(Product::getPrice).sum();
       Order order = new Order(UUID.randomUUID(),userId,total_price,cart.getProducts());
        userRepository.addOrderToUser(userId,order);
        emptyCart(userId);

    }
    public void emptyCart(UUID userId) {
        Cart cart = cartRepository.getCartByUserId(userId);

        if (cart == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found for this user");
        }

        // Get all carts from the repository
        ArrayList<Cart> carts = cartRepository.getCarts();

        // Find the cart in the repository and clear its products
        for (Cart c : carts) {
            if (c.getUserId().equals(userId)) {
                c.getProducts().clear(); // Clear the cart
                break; // Stop after finding the cart
            }
        }

        // Persist the updated carts list
        cartRepository.overrideData(carts);
    }


    public void removeOrderFromUser(UUID userId, UUID orderId) {
        // Retrieve all users
        ArrayList<User> users = userRepository.findAll();

        // Find the user by ID
        for (User user : users) {
            if (user.getId().equals(userId)) {
                // Remove the order from the user's order list
                user.getOrders().removeIf(order -> order.getId().equals(orderId));
                break; // Stop after finding the user
            }
        }

        // Save updated user data
        userRepository.overrideData(users);
    }

}
