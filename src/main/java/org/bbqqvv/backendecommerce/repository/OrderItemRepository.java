package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.OrderItem;
import org.bbqqvv.backendecommerce.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    void deleteAllByOrderId(Long orderId);
    List<OrderItem> findByProductIdAndOrderUserIdAndOrderStatus(
            Long productId,
            Long userId,
            OrderStatus status
    );
}
