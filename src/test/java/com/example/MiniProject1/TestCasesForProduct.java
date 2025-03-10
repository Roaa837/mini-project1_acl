package com.example.MiniProject1;

import static org.junit.jupiter.api.Assertions.*;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.service.CartService;
import com.example.service.OrderService;
import com.example.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@SpringBootTest
public class TestCasesForProduct {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testAddProduct_Success() {
        Product product = new Product("Laptop", 1500.00);

        Product savedProduct = productService.addProduct(product);

        assertNotNull(savedProduct.getId());
        assertEquals("Laptop", savedProduct.getName());
        assertEquals(1500.00, savedProduct.getPrice());
    }

    @Test
    void testAddProduct_Failure() {
        Product product = null;

        assertThrows(NullPointerException.class, () -> productService.addProduct(product));
    }

    @Test
    void testAddProduct_EdgeCase_ZeroPrice() {
        Product product = new Product("Free Sample", 0.0);

        Product savedProduct = productService.addProduct(product);

        assertNotNull(savedProduct.getId());
        assertEquals(0.0, savedProduct.getPrice());
    }

    @Test
    void testGetProducts_Success() {
        productService.addProduct(new Product("Phone", 800.00));

        ArrayList<Product> products = productService.getProducts();

        assertFalse(products.isEmpty());
    }

    @Test
    void testGetProducts_Failure() {
        ArrayList<Product> products = productService.getProducts();

        assertEquals(0, products.size());
    }

    @Test
    void testGetProducts_EdgeCase_EmptyList() {

        ArrayList<Product> products = productService.getProducts();

        assertTrue(products.isEmpty());
    }

    @Test
    void testGetProductById_Success() {
        Product product = productService.addProduct(new Product("Tablet", 500.00));
        UUID productId = product.getId();

        Product foundProduct = productService.getProductById(productId);

        assertEquals("Tablet", foundProduct.getName());
    }

    @Test
    void testGetProductById_Failure() {
        UUID invalidId = UUID.randomUUID();

        assertThrows(RuntimeException.class, () -> productService.getProductById(invalidId));
    }

    @Test
    void testGetProductById_EdgeCase_NullId() {
        assertThrows(NullPointerException.class, () -> productService.getProductById(null));
    }

    @Test
    void testDeleteProductById_Success() {
        Product product = productService.addProduct(new Product("Monitor", 300.00));
        UUID productId = product.getId();

        productService.deleteProductById(productId);

        assertThrows(RuntimeException.class, () -> productService.getProductById(productId));
    }

    @Test
    void testDeleteProductById_Failure() {
        UUID invalidId = UUID.randomUUID();

        assertThrows(RuntimeException.class, () -> productService.deleteProductById(invalidId));
    }

    @Test
    void testDeleteProductById_EdgeCase_NullId() {
        assertThrows(NullPointerException.class, () -> productService.deleteProductById(null));
    }

    @Test
    void testUpdateProduct_Success() {
        Product product = productService.addProduct(new Product("Keyboard", 50.00));
        UUID productId = product.getId();

        Product updatedProduct = productService.updateProduct(productId, "Mechanical Keyboard", 70.00);

        assertEquals("Mechanical Keyboard", updatedProduct.getName());
        assertEquals(70.00, updatedProduct.getPrice());
    }

    @Test
    void testUpdateProduct_Failure() {
        UUID invalidId = UUID.randomUUID();

        assertThrows(RuntimeException.class, () -> productService.updateProduct(invalidId, "Invalid Update", 999.99));
    }

    @Test
    void testUpdateProduct_EdgeCase_NullValues() {
        Product product = productService.addProduct(new Product("Mouse", 25.00));
        UUID productId = product.getId();

        assertThrows(NullPointerException.class, () -> productService.updateProduct(productId, null, 30.00));
    }

    @Test
    void testApplyDiscount_Success() {
        Product product = productService.addProduct(new Product("Headphones", 100.00));
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(product.getId());

        productService.applyDiscount(10, productIds);

        Product discountedProduct = productService.getProductById(product.getId());
        assertEquals(90.00, discountedProduct.getPrice());
    }

    @Test
    void testApplyDiscount_Failure() {
        ArrayList<UUID> invalidIds = new ArrayList<>();
        invalidIds.add(UUID.randomUUID());

        assertThrows(RuntimeException.class, () -> productService.applyDiscount(10, invalidIds));
    }

    @Test
    void testApplyDiscount_EdgeCase_ZeroDiscount() {
        Product product = productService.addProduct(new Product("Charger", 20.00));
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(product.getId());

        productService.applyDiscount(0, productIds);

        Product samePriceProduct = productService.getProductById(product.getId());
        assertEquals(20.00, samePriceProduct.getPrice());
    }
}
