package org.bbqqvv.backendecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long productId;      // ID sản phẩm
    private int quantity;        // Số lượng
    private BigDecimal price;    // Giá sản phẩm
    private ProductResponse product; // Thông tin sản phẩm, bao gồm ảnh
}
