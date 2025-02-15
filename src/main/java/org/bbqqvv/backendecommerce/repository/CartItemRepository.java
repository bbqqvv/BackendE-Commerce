package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Tìm một sản phẩm cụ thể trong giỏ hàng dựa trên Cart ID, Product ID, Size và Color
     */
    Optional<CartItem> findByCartIdAndProductIdAndSizeNameAndColor(Long cartId, Long productId, String sizeName, String color);

    /**
     * Xóa tất cả sản phẩm trong giỏ hàng dựa trên Cart ID
     */
    void deleteAllByCartId(Long cartId);
}
