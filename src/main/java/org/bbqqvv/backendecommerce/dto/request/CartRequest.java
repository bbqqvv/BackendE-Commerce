package org.bbqqvv.backendecommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CartRequest {
    @NotNull(message = "User ID cannot be null")
    private Long userId; // ID người dùng

    @NotNull(message = "Cart items cannot be null")
    private List<CartItemRequest> items; // Các sản phẩm trong giỏ hàng
}
