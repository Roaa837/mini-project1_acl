package com.example.controller;
import com.example.model.Product;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @GetMapping("/")
    public ArrayList<Product> getProducts(){
        return productService.getProducts();
    }

    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable UUID productId)
    {
        return productService.getProductById(productId);
    }

    @DeleteMapping("/delete/{productId}")
    public String deleteProductById(@PathVariable UUID productId) {
        try {
            productService.deleteProductById(productId);
            return "Product deleted successfully";
        } catch (Exception e) {
            return "Error deleting product with ID " + productId + ": " + e.getMessage();
        }
    }

    @PutMapping("/update/{productId}")
    public Product updateProduct(@PathVariable UUID productId, @RequestBody Map<String, Object> body) {
        System.out.println("Received request body: " + body);  // Debugging output

        if (body == null || !body.containsKey("newName") || !body.containsKey("newPrice")) {
            throw new IllegalArgumentException("Missing required fields: newName or newPrice");
        }

        String newName = (String) body.get("newName");
        Object priceObj = body.get("newPrice");

        // Ensure price is convertible to double
        double newPrice;
        try {
            newPrice = Double.parseDouble(priceObj.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid price value");
        }

        return productService.updateProduct(productId, newName, newPrice);
    }




    @PutMapping("/applyDiscount")
    public String applyDiscount(@RequestParam double discount,@RequestBody ArrayList<UUID> productIds) {
        try {
            productService.applyDiscount(discount, productIds);
            return "Discount applied successfully";
        } catch (Exception e) {
            return "Error applying discount: " + e.getMessage();
        }
    }
}

