package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Optional<Discount> findByCode(String discountCode);
    boolean existsByCode(String code);

}
