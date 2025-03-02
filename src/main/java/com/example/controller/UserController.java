package com.example.controller;

import com.example.model.Order;
import com.example.model.User;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/")
    public User addUser(@RequestBody User user)
    {
        return userService.addUser(user);
    }

    @GetMapping("/")
    public ArrayList<User> getUsers()
    {
        return userService.getUsers();
    }
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable UUID userId)
    {
        return userService.getUserById(userId);
    }
    @GetMapping("/{userId}/orders")
    public List<Order> getOrdersByUserId(@PathVariable UUID userId)
    {
        return userService.getOrdersByUserId(userId);
    }


    @DeleteMapping("/delete/{userId}")
    public String deleteUserById(@PathVariable UUID userId) {
        try {
            userService.deleteUserById(userId);
            return "User with ID " + userId + " was successfully deleted.";
        } catch (Exception e) {
            return "Error deleting user with ID " + userId + ": " + e.getMessage();
        }
    }


}
