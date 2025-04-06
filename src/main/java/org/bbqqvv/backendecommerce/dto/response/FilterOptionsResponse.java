package org.bbqqvv.backendecommerce.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FilterOptionsResponse {
    private List<String> categories;
    private List<String> tags;
    private List<String> colors;
    private List<String> sizes;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
