package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.SizeProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SizeProductRepository extends JpaRepository<SizeProduct, Long> {
    Optional<SizeProduct> findBySizeName(String sizeName);

    Optional<SizeProduct> findByProductVariantSizes_ProductVariant_Product_IdAndSizeName(Long productId, String sizeName);
}
