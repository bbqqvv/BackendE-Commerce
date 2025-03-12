package org.bbqqvv.backendecommerce.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
    private String color;
    private String sizeName;
}


