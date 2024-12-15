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
public class ProductVariantResponse {
    private Long id;
    private String size;
    private String color;
    private BigDecimal price;

    public ProductVariantResponse(String color, String size, BigDecimal price) {
        this.color = color;
        this.size = size;
        this.price = price;
    }
}
