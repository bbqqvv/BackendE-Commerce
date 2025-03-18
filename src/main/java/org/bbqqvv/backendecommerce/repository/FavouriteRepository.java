package org.bbqqvv.backendecommerce.repository;

import io.micrometer.common.lang.NonNull;
import org.bbqqvv.backendecommerce.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    List<Favourite> findByUserId(Long userId);
    @NonNull
    Optional<Favourite> findById(Long id);

    boolean existsByProductIdAndUserId(Long productId, Long id);
}
