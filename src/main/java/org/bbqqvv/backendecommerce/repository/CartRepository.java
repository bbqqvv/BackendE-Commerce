package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
