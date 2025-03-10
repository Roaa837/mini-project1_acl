package com.example.controller;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.service.CartService;
import com.example.service.ProductService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController

@RequestMapping("/user")
public class UserController {
    @Autowired
    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;


    public UserController(UserService userService, ProductService productService, CartService cartService) {
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
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
            return "User deleted successfully";
        } catch (Exception e) {
            return "User not found";
        }
    }
    @PostMapping("/{userId}/removeOrder")
    public String removeOrderFromUser(@PathVariable UUID userId, @RequestParam UUID orderId)
    {
        try{
            userService.removeOrderFromUser(userId, orderId);
            return "Order removed successfully";
        }catch(Exception e){
            return "Error deleting user with ID " + userId + ": " + e.getMessage();
        }

    }
    @DeleteMapping("/{userId}/emptyCart")
    public String emptyCart(@PathVariable UUID userId){
        try{
            userService.emptyCart(userId);
            return "Cart emptied successfully";
        }catch (Exception e){
            return "Error deleting user with ID " + userId + ": " + e.getMessage();
        }
    }
    @PutMapping("/addProductToCart")
    public String addProductToCart(@RequestParam UUID userId, @RequestParam UUID productId) {
        try {
            Cart cart = cartService.getCartByUserId(userId);
            System.out.println(cart);
            if (cart == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found for user");
            }

            Product product = productService.getProductById(productId);
            if (product == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
            }

            cartService.addProductToCart(cart.getId(), product);
            return "Product added to cart successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to add product to cart");
        }
    }
    @PutMapping("/deleteProductFromCart")
    public String deleteProductFromCart(@RequestParam UUID userId, @RequestParam UUID productId){
        try{
            Cart cart = cartService.getCartByUserId(userId);
            if (cart == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found for user");
            }
            Product product = productService.getProductById(productId);
            if (product == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
            }
            cartService.deleteProductFromCart(cart.getId(), product);
            return "Product deleted from cart";
        }
        catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to remove product from cart");
        }
    }
    @PostMapping("/{userId}/checkout")
    public String addOrderToUser(@PathVariable UUID userId){
        try{
            userService.addOrderToUser(userId);
            return "Order added successfully";
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to add order to user");
        }

    }

}
