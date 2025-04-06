package org.bbqqvv.backendecommerce.repository;


import org.bbqqvv.backendecommerce.entity.Category;
import org.bbqqvv.backendecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsByProductCode(String productCode);
    Page<Product> findProductByCategory(Category category, Pageable pageable);
    boolean existsBySlug(String slug);
    Optional<Product> findBySlug(String slug);
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("SELECT DISTINCT pv.color FROM ProductVariant pv")
    List<String> findDistinctColors();

    @Query("SELECT DISTINCT sp.sizeName FROM SizeProduct sp")
    List<String> findDistinctSizes();

    @Query("SELECT DISTINCT t.name FROM Product p JOIN p.tags t")
    List<String> findDistinctTags();

    @Query("SELECT MIN(sp.price) FROM SizeProduct sp")
    BigDecimal findMinPrice();

    @Query("SELECT MAX(sp.price) FROM SizeProduct sp")
    BigDecimal findMaxPrice();

}