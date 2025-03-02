package com.example.service;

import com.example.model.Order;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("rawtypes")
@Service

public class UserService extends MainService<User> {
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User addUser(User user) {
        return userRepository.addUser(user);
    }
    public ArrayList<User> getUsers() {
        return new ArrayList<>(userRepository.findAll());
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
}
