package org.bbqqvv.backendecommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bbqqvv.backendecommerce.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    private Long id;
    private Long userId;
    private String name;
    private String address;
    private String phoneNumber;
    private String notes;
    private String orderCode;
    private String status;
    private PaymentMethod paymentMethod;
    private BigDecimal shippingFee;
    private String discountCode;
    private BigDecimal discountAmount;
    private LocalDate expectedDeliveryDate;
    private List<OrderItemResponse> orderItems;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
