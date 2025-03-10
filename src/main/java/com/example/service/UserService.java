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



    public UserService(UserRepository userRepository, CartService cartService) {
        this.userRepository = userRepository;

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
    public void emptyCart(UUID userId)
    {
        Cart cart =  cartRepository.getCartById(userId);
        if (cart == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found for this user");
        }
        cart.getProducts().clear();
        cartRepository.overrideData(cartRepository.getCarts());
    }

    public void removeOrderFromUser(UUID userId, UUID orderId){
        userRepository.removeOrderFromUser(userId, orderId);
    }
}
