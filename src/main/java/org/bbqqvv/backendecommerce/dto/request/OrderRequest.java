package org.bbqqvv.backendecommerce.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bbqqvv.backendecommerce.entity.PaymentMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    private Long addressId;
    private Long cartId;
    private String discountCode;
    private PaymentMethod paymentMethod;
}
