package org.bbqqvv.backendecommerce.dto.response;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class FavouriteResponse {
    private Long id;
    private Long userId;
    private String nameProduct;
    private String imageUrl;
    private BigDecimal price;
}
