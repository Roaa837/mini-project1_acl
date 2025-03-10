package com.example.service;

import com.example.model.Product;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.repository.ProductRepository;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class ProductService extends MainService<Product> {

    @Autowired
    private final ProductRepository ProductRepository;

    public ProductService(ProductRepository ProductRepository) {
        this.ProductRepository = ProductRepository;
    }

    public Product addProduct(Product product){
        return ProductRepository.addProduct(product);
    }

    public ArrayList<Product> getProducts(){
        return new ArrayList<>(ProductRepository.findAll());
    }

    public Product getProductById(UUID productId){
        return ProductRepository.getProductById(productId);
    }

    public void deleteProductById(UUID productId){
        ProductRepository.deleteProductById(productId);
    }

    public Product updateProduct(UUID productId, String newName, double newPrice){
        return ProductRepository.updateProduct(productId, newName, newPrice);
    }

    public void applyDiscount(double discount, ArrayList<UUID> productIds){
        ProductRepository.applyDiscount(discount, productIds);
    }
}
