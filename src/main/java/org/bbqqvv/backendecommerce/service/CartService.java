package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.request.CartRequest;
import org.bbqqvv.backendecommerce.dto.response.CartResponse;
import org.bbqqvv.backendecommerce.entity.Cart;
import java.util.List;

public interface CartService {

    // Thêm sản phẩm vào giỏ hàng
    CartResponse addProductToCart(CartRequest cartRequest);

    // Cập nhật thông tin giỏ hàng (số lượng sản phẩm, v.v.)
    CartResponse updateCart(CartRequest cartRequest);

    // Xóa sản phẩm khỏi giỏ hàng
    CartResponse removeProductFromCart(Long productId);

    // Lấy giỏ hàng của người dùng
    CartResponse getCartByUserId(Long userId);

    // Lấy tất cả các giỏ hàng (thường chỉ dành cho admin)
    List<CartResponse> getAllCarts();

    // Xóa giỏ hàng của người dùng (ví dụ sau khi thanh toán)
    void clearCart(Long userId);
}
