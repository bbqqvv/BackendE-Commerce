package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    void deleteAllByOrderId(Long orderId);
}
