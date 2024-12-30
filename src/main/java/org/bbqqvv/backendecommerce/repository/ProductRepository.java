package org.bbqqvv.backendecommerce.repository;


import org.bbqqvv.backendecommerce.entity.Category;
import org.bbqqvv.backendecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByProductCode(String productCode);
    List<Product> findProductByCategory(Category category);
    boolean existsBySlug(String slug);
    Optional<Product> findBySlug(String slug);
}
