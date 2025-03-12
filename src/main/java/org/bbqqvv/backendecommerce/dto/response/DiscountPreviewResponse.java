package org.bbqqvv.backendecommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bbqqvv.backendecommerce.entity.DiscountType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountPreviewResponse {
    String discountCode;          // Mã giảm giá được áp dụng
    DiscountType discountType;    // Loại giảm giá (PERCENTAGE, MONEY)
    BigDecimal originalTotalAmount;   // Tổng tiền trước khi giảm giá
    BigDecimal discountAmount;        // Số tiền được giảm
    BigDecimal finalAmount;           // Tổng tiền sau khi giảm giá
    Boolean valid;                // Mã giảm giá hợp lệ hay không
    String message;               // Thông báo (ví dụ: "Mã hợp lệ", "Mã đã hết hạn", ...)
}
