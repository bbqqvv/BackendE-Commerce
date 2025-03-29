package org.bbqqvv.backendecommerce.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String mainImageUrl;
    private int quantity;
    private String color;
    private String sizeName;
    private BigDecimal price;
    private BigDecimal subtotal;
    private int stock;
    private boolean inStock;
}
