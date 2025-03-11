package com.example.repository;

import com.example.model.Product;
import jdk.jfr.Registered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Registered

@Repository
@SuppressWarnings("rawtypes")
public class ProductRepository extends MainRepository<Product> {

    public ProductRepository() {
    }

    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/products.json";
    }

    @Override
    protected Class<Product[]> getArrayType() {
        return Product[].class;
    }

    public ArrayList<Product> getProducts() {
        try {
            return new ArrayList<>(findAll());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve products from products.json");
        }
    }

    public Product getProductById(UUID productId) {
        if (productId == null) {
            throw new NullPointerException("Product ID cannot be null");
        }
        try {
            List<Product> products = findAll();

            return products.stream()
                    .filter(product ->product!=null && product.getId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve product from products.json");
        }
    }

    public Product addProduct(Product product){
        try{
            ArrayList<Product> products = findAll();
            if (product != null && product.getId() == null){
                product.setId(UUID.randomUUID());
            }
            if(product != null){
                products.add(product);
                overrideData(products);
            }
            return product;
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to add product to products.json");
        }
    }

    public void deleteProductById(UUID productId){
        if (productId == null) {
            throw new NullPointerException("Product ID cannot be null");
        }
        try{
            ArrayList<Product> products = findAll();
            boolean removed = products.removeIf(product ->product!=null && product.getId().equals(productId));
           if (!removed) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
           }
           overrideData(products);

        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete product from products.json");
        }
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        if(productId == null || newName == null){
            throw new NullPointerException("Product ID & product name have to be defined");
        }
        try {
            ArrayList<Product> products = findAll();
            Product product = products.stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            product.setName(newName);
            product.setPrice(newPrice);

            overrideData(products);
            return product;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update product in products.json");
        }
    }


    public void applyDiscount(double discount, ArrayList<UUID> productIds){
        try{
            ArrayList<Product> products = findAll();
            for (Product product : products){
                if (productIds.contains(product.getId())){
                    double newPrice = product.getPrice() * (1 - discount/100);
                    product.setPrice(newPrice);
                }
            }
            overrideData(products);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to apply discount to products in products.json");
        }
    }


}
