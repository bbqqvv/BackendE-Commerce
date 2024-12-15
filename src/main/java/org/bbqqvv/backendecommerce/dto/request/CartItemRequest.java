package org.bbqqvv.backendecommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    @NotNull(message = "Product ID cannot be null")
    private Long productId; // ID của sản phẩm

    @Positive(message = "Quantity must be greater than 0")
    private int quantity; // Số lượng sản phẩm

    @NotNull(message = "Price cannot be null")
    private BigDecimal price; // Giá sản phẩm
}
