package org.bbqqvv.backendecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long id; // ID của giỏ hàng
    private UserResponse user; // Thông tin người dùng
    private List<CartItemResponse> items; // Danh sách các sản phẩm trong giỏ hàng
    private BigDecimal totalPrice; // Tổng giá tiền của giỏ hàng

}
