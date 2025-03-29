package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.Order;
import org.bbqqvv.backendecommerce.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    boolean existsByUserIdAndOrderItems_Product_IdAndStatus(Long userId, Long productId, OrderStatus status);
}
