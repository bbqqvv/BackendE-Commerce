package org.bbqqvv.backendecommerce.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DiscountRequest {
    private String code;
    private BigDecimal discountAmount;
    private boolean isPercentage;
    private LocalDateTime expiryDate;
    private boolean isActive;
}
