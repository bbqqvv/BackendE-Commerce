package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.Order;
import org.bbqqvv.backendecommerce.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findAll(Pageable pageable);
    boolean existsByUserIdAndOrderItems_Product_IdAndStatus(Long userId, Long productId, OrderStatus status);
    Optional<Order> findByOrderCode(String orderCode);
}
