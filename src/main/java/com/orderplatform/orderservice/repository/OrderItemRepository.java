
package com.orderplatform.orderservice.repository;

import com.orderplatform.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
