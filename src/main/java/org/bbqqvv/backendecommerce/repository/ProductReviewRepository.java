package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    Optional<ProductReview> findByProductIdAndUserId(Long productId, Long userId);

    Page<ProductReview> findByProductId(Long productId, Pageable pageable);

    Page<ProductReview> findByUserId(Long userId, Pageable pageable);
}
