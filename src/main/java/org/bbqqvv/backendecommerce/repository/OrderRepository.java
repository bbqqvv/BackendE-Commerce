package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findAll(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "FROM Order o JOIN o.orderItems oi " +
            "WHERE o.user.id = :userId " +
            "AND oi.product.id = :productId " +
            "AND o.status = 'DELIVERED'")
    boolean existsByUserAndProductAndDelivered(
            @Param("userId") Long userId,
            @Param("productId") Long productId);

    Optional<Order> findByOrderCode(String orderCode);
}