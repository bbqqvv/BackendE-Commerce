package org.bbqqvv.backendecommerce.repository;


import org.bbqqvv.backendecommerce.entity.Category;
import org.bbqqvv.backendecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByProductCode(String productCode);
    Page<Product> findProductByCategory(Category category, Pageable pageable);
    boolean existsBySlug(String slug);
    Optional<Product> findBySlug(String slug);
    Page<Product> findAll(Pageable pageable);
}