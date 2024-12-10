package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Các phương thức truy vấn tùy chỉnh nếu cần
}
