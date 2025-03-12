package org.bbqqvv.backendecommerce.dto.request;

import lombok.*;
import org.bbqqvv.backendecommerce.entity.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DiscountRequest {
    private String code;
    private BigDecimal discountAmount;
    private BigDecimal maxDiscountAmount;
    private DiscountType discountType;
    private BigDecimal minOrderValue;
    private Integer usageLimit;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private boolean active;
    private List<Long> applicableProducts; // Danh sách ID sản phẩm được áp dụng
    private List<Long> applicableUsers; // Danh sách ID người dùng được áp dụng
}
