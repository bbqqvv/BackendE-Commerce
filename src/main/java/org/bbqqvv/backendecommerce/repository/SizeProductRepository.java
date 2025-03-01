package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.SizeProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SizeProductRepository extends JpaRepository<SizeProduct, Long> {
    Optional<SizeProduct> findBySizeName(String sizeName);

    @Query("SELECT sp FROM SizeProduct sp " +
            "JOIN sp.productVariantSizes pvs " +
            "JOIN pvs.productVariant pv " +
            "WHERE pv.product.id = :productId " +
            "AND LOWER(TRIM(sp.sizeName)) = LOWER(TRIM(:sizeName))")
    Optional<SizeProduct> findByProductIdAndSizeName(@Param("productId") Long productId, @Param("sizeName") String sizeName);

}
