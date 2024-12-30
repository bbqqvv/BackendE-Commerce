package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    List<Favourite> findByUserId(Long userId);

    Optional<Favourite> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByProductIdAndUserId(Long productId, Long id);
}
