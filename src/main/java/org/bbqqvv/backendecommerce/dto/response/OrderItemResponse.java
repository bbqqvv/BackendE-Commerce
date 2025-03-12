package org.bbqqvv.backendecommerce.dto.response;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private String mainImageUrl;
    private String color;
    private String sizeName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

}
