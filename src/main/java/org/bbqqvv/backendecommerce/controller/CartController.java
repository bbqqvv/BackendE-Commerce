package org.bbqqvv.backendecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.bbqqvv.backendecommerce.dto.request.CartRequest;
import org.bbqqvv.backendecommerce.dto.response.CartResponse;
import org.bbqqvv.backendecommerce.service.CartService;
import org.bbqqvv.backendecommerce.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    public ApiResponse<CartResponse> addProductToCart(@RequestBody CartRequest cartRequest) {
        return ApiResponse.<CartResponse>builder()
                .data(cartService.addProductToCart(cartRequest))
                .build();
    }

    // Cập nhật giỏ hàng
    @PutMapping("/update")
    public ApiResponse<CartResponse> updateCart(@RequestBody CartRequest cartRequest) {
        return ApiResponse.<CartResponse>builder()
                .data(cartService.updateCart(cartRequest))
                .build();
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/remove/{productId}")
    public ApiResponse<CartResponse> removeProductFromCart(@PathVariable Long productId) {
        return ApiResponse.<CartResponse>builder()
                .data(cartService.removeProductFromCart(productId))
                .build();
    }

    // Lấy thông tin giỏ hàng của người dùng
    @GetMapping("/user/{userId}")
    public ApiResponse<CartResponse> getCartByUserId(@PathVariable Long userId) {
        return ApiResponse.<CartResponse>builder()
                .data(cartService.getCartByUserId(userId))
                .build();
    }

    // Lấy tất cả giỏ hàng
    @GetMapping("/all")
    public ApiResponse<List<CartResponse>> getAllCarts() {
        return ApiResponse.<List<CartResponse>>builder()
                .data(cartService.getAllCarts())
                .build();
    }

    // Xóa tất cả sản phẩm trong giỏ hàng của người dùng
    @DeleteMapping("/clear/{userId}")
    public ApiResponse<String> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ApiResponse.<String>builder()
                .data("Cart cleared successfully")
                .build();
    }
}
