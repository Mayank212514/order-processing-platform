package com.orderplatform.orderservice.service;

import com.orderplatform.orderservice.entity.Product;
import com.orderplatform.orderservice.repository.ProductRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.orderplatform.orderservice.dto.ProductRequest;
import com.orderplatform.orderservice.dto.ProductResponse;


import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getAllProducts() {
    return productRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public ProductResponse createProduct(ProductRequest request) {
    Product product = new Product(
            request.getName(),
            request.getPrice(),
            request.getStock()
    );

    Product savedProduct = productRepository.save(product);
    return toResponse(savedProduct);
    }

    private ProductResponse toResponse(Product product) {
    return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getStock()
    )   ;
    }

    public ProductResponse getProductById(Long id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found with id: " + id
            ));

        return toResponse(product);
    }


    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found with id: " + id
            ));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        Product updatedProduct = productRepository.save(product);

        return toResponse(updatedProduct);
    }


    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found with id: " + id
            ));

        productRepository.delete(product);
    }

}
