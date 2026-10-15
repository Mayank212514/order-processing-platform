
package com.orderplatform.orderservice.controller;

import com.orderplatform.orderservice.entity.Product;
import com.orderplatform.orderservice.repository.OrderItemRepository;
import com.orderplatform.orderservice.repository.OrderRepository;
import com.orderplatform.orderservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    void shouldCreateOrderAndReduceStock() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(10);
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "productId": %d,
                          "quantity": 2
                        }
                        """.formatted(savedProduct.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalAmount").value(200.00))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        Product updatedProduct = productRepository.findById(savedProduct.getId())
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(8, updatedProduct.getStock());
        org.junit.jupiter.api.Assertions.assertEquals(1, orderRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(1, orderItemRepository.count());
    }
    

    @Test
    void shouldRejectOrderWhenStockIsInsufficient() throws Exception {
        Product product = new Product();
        product.setName("Limited Stock Product");
        product.setPrice(new BigDecimal("50.00"));
        product.setStock(1);

        Product savedProduct = productRepository.save(product);

        long ordersBefore = orderRepository.count();
        long orderItemsBefore = orderItemRepository.count();

        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "productId": %d,
                      "quantity": 2
                    }
                    """.formatted(savedProduct.getId())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error")
                    .value("Insufficient stock for product: Limited Stock Product"));

        Product unchangedProduct = productRepository.findById(savedProduct.getId())
            .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
            1, unchangedProduct.getStock());

        org.junit.jupiter.api.Assertions.assertEquals(
            ordersBefore, orderRepository.count());

        org.junit.jupiter.api.Assertions.assertEquals(
            orderItemsBefore, orderItemRepository.count());
    }

    @Test
    void shouldRejectOrderWhenProductDoesNotExist() throws Exception {
        long ordersBefore = orderRepository.count();

        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "productId": 999999,
                      "quantity": 1
                    }
                    """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error")
                    .value("Product not found with id: 999999"));

        org.junit.jupiter.api.Assertions.assertEquals(
            ordersBefore, orderRepository.count());
    }

    @Test
    void shouldRejectOrderWhenQuantityIsZero() throws Exception {
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "productId": 1,
                      "quantity": 0
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void shouldReturnAllOrders() throws Exception {
        Product product = new Product();
        product.setName("Order List Test Product");
        product.setPrice(new BigDecimal("25.00"));
        product.setStock(5);

        Product savedProduct = productRepository.save(product);

        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "productId": %d,
                      "quantity": 2
                    }
                    """.formatted(savedProduct.getId())))
            .andExpect(status().isCreated());

        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .get("/api/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath(
                    "$[?(@.totalAmount == 50.00 && @.status == 'CONFIRMED')]"
            ).isNotEmpty());
    }

    @Test
    void shouldReturnOrderById() throws Exception {
        Product product = new Product();
        product.setName("Single Order Test Product");
        product.setPrice(new BigDecimal("30.00"));
        product.setStock(5);

        Product savedProduct = productRepository.save(product);

        String response = mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "productId": %d,
                      "quantity": 2
                    }
                    """.formatted(savedProduct.getId())))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String idValue = response.replaceAll(
            ".*\\\"id\\\":(\\d+).*", "$1");
        Long orderId = Long.parseLong(idValue);

        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .get("/api/orders/" + orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(orderId))
            .andExpect(jsonPath("$.totalAmount").value(60.00))
            .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .get("/api/orders/999999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error")
                    .value("Order not found with id: 999999"));
    }
}
