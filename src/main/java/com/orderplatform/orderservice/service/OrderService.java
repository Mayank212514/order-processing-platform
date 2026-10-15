
package com.orderplatform.orderservice.service;

import com.orderplatform.orderservice.entity.Order;
import com.orderplatform.orderservice.entity.OrderItem;
import com.orderplatform.orderservice.entity.Product;
import com.orderplatform.orderservice.repository.OrderItemRepository;
import com.orderplatform.orderservice.repository.OrderRepository;
import com.orderplatform.orderservice.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.orderplatform.orderservice.dto.OrderRequest;
import com.orderplatform.orderservice.dto.OrderResponse;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        Long productId = request.getProductId();
        int quantity = request.getQuantity();

        if (quantity <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Quantity must be greater than zero"
            );
        }

        Product product = productRepository.findByIdForUpdate(productId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found with id: " + productId
            ));

        if (product.getStock() < quantity) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Insufficient stock for product: " + product.getName()
            );
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        BigDecimal totalAmount = product.getPrice()
            .multiply(BigDecimal.valueOf(quantity));

        Order order = new Order(totalAmount, "CONFIRMED");
        Order savedOrder = orderRepository.save(order);

        OrderItem orderItem = new OrderItem(
            savedOrder,
            product,
            quantity,
            product.getPrice()
        );

        orderItemRepository.save(orderItem);

        return new OrderResponse(
            savedOrder.getId(),
            savedOrder.getTotalAmount(),
            savedOrder.getStatus(),
            savedOrder.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
            .stream()
            .map(order -> new OrderResponse(
                    order.getId(),
                    order.getTotalAmount(),
                    order.getStatus(),
                    order.getCreatedAt()
        ))
        .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Order not found with id: " + id
            ));

        return new OrderResponse(
            order.getId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt()
        );
    }

}
