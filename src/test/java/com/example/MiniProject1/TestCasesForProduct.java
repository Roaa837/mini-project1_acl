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
    void testAddProduct_S() {
        Product p = new Product(UUID.randomUUID(),"v-cola", 15.00);

        Product product = productService.addProduct(p);

        assertNotNull(product);
        assertEquals(product.getId(), p.getId());
    }

    @Test
    void testAddProduct_F() {
        Product product = productService.addProduct(null);

        assertNull(product);
    }

    @Test
    void testAddProduct_E() {
        Product p = new Product(UUID.randomUUID(),"Free", 0.0);

        Product product = productService.addProduct(p);

        assertNotNull(product);
        assertNotNull(product.getPrice());
        assertEquals(0.0, product.getPrice());
    }

    @Test
    void testGetProducts_S() {
        productRepository.overrideData(new ArrayList<>());
        productService.addProduct(new Product(UUID.randomUUID(),"I-Phone", 18000.00));

        ArrayList<Product> products = productService.getProducts();

        assertNotNull(products);
        assertEquals(products.size(), 1);
    }

    @Test
    void testGetProducts_F() {
        productRepository.overrideData(new ArrayList<>());
        ArrayList<Product> products = productService.getProducts();

        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void testGetProducts_E() {
        productRepository.overrideData(new ArrayList<>());
        ArrayList<Product> products = productService.getProducts();

        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void testGetProductById_S() {
        UUID productId = UUID.randomUUID();
        productService.addProduct(new Product(productId,"Table", 500.00));

        Product product = productService.getProductById(productId);

        assertNotNull(product);
        assertEquals(product.getId(), productId);
        assertEquals("Table", product.getName());
    }

    @Test
    void testGetProductById_F() {
        UUID UnAvailableId = UUID.randomUUID();

        assertThrows(RuntimeException.class, () -> productService.getProductById(UnAvailableId));
    }

    @Test
    void testGetProductById_E() {
        assertThrows(NullPointerException.class, () -> productService.getProductById(null));
    }

    @Test
    void testDeleteProductById_S() {
        UUID productId = UUID.randomUUID();
        productService.addProduct(new Product(productId,"Monitor", 300.00));

        productService.deleteProductById(productId);

        assertThrows(RuntimeException.class, () -> productService.getProductById(productId));
    }

    @Test
    void testDeleteProductById_F() {
        UUID UnavailableId = UUID.randomUUID();

        assertThrows(RuntimeException.class, () -> productService.deleteProductById(UnavailableId));
    }

    @Test
    void testDeleteProductById_E() {
        assertThrows(NullPointerException.class, () -> productService.deleteProductById(null));
    }

    @Test
    void testUpdateProduct_S() {
        UUID productId = UUID.randomUUID();
        productService.addProduct(new Product(productId,"Keyboard", 50.00));

        productService.updateProduct(productId, "upgraded Keyboard", 70.00);

        Product product = productService.getProductById(productId);

        assertEquals("upgraded Keyboard", product.getName());
        assertEquals(70.00, product.getPrice());
    }

    @Test
    void testUpdateProduct_F() {
        UUID UnAvailableId = UUID.randomUUID();

        assertThrows(RuntimeException.class, () -> productService.updateProduct(UnAvailableId, "Invalid product", 999.99));
    }

    @Test
    void testUpdateProduct_E() {
        UUID productId = UUID.randomUUID();
        Product product = productService.addProduct(new Product(productId,"Mouse", 25.00));

        assertThrows(NullPointerException.class, () -> productService.updateProduct(productId, null, 30.00));
    }

    @Test
    void testApplyDiscount_S() {
        Product product = productService.addProduct(new Product("Headphones", 100.00));
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(product.getId());

        productService.applyDiscount(10, productIds);

        Product discountedProduct = productService.getProductById(product.getId());
        assertEquals(90.00, discountedProduct.getPrice());
    }

    @Test
    void testApplyDiscount_F() {
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(UUID.randomUUID());

        ArrayList<Product> productsBefore = productService.getProducts();
        productService.applyDiscount(10, productIds);
        ArrayList<Product> productsAfter = productService.getProducts();

        for (int i = 0; i < productsAfter.size(); i++) {
            assertEquals(productsBefore.get(i).getPrice(), productsAfter.get(i).getPrice() );
        }
    }

    @Test
    void testApplyDiscount_E() {
        Product product = productService.addProduct(new Product("Charger", 20.00));
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(product.getId());

        productService.applyDiscount(0, productIds);

        Product samePriceProduct = productService.getProductById(product.getId());
        assertEquals(20.00, samePriceProduct.getPrice());
    }
}
