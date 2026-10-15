
package com.orderplatform.orderservice.repository;

import com.orderplatform.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
