package org.bbqqvv.backendecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bbqqvv.backendecommerce.dto.ApiResponse;
import org.bbqqvv.backendecommerce.dto.request.CartRequest;
import org.bbqqvv.backendecommerce.dto.response.CartResponse;
import org.bbqqvv.backendecommerce.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * Thêm hoặc cập nhật sản phẩm trong giỏ hàng
     */
    @PostMapping("/add-or-update")
    public ApiResponse<CartResponse> addOrUpdateProductInCart(@RequestBody @Valid CartRequest cartRequest) {
        return ApiResponse.<CartResponse>builder()
                .data(cartService.addOrUpdateProductInCart(cartRequest))
                .message("Cart updated successfully")
                .build();
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng theo productId, size, color
     */
    @DeleteMapping("/remove")
    public ApiResponse<CartResponse> removeProductFromCart(
            @RequestParam Long productId,
            @RequestParam String sizeName,
            @RequestParam String color) {

        return ApiResponse.<CartResponse>builder()
                .data(cartService.removeProductFromCart(productId, sizeName, color))
                .message("Product removed from cart")
                .build();
    }

    /**
     * Lấy giỏ hàng của người dùng hiện tại
     */
    @GetMapping
    public ApiResponse<CartResponse> getCartByUser() {
        return ApiResponse.<CartResponse>builder()
                .data(cartService.getCartByUserId())
                .message("Cart retrieved successfully")
                .build();
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    @DeleteMapping("/clear")
    public ApiResponse<String> clearCart() {
        cartService.clearCart();
        return ApiResponse.<String>builder()
                .data("Cart cleared successfully")
                .message("Cart has been emptied")
                .build();
    }
}
