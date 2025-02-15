package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.SizeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SizeCategoryRepository extends JpaRepository<SizeCategory, Long> {
    @Query("SELECT s.name FROM SizeCategory s WHERE s.category.id = :categoryId")
    List<String> findSizesByCategory(@Param("categoryId") Long categoryId);
}
