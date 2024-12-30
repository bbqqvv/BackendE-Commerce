package org.bbqqvv.backendecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String shortDescription;
    private String description;
    private String productCode;
    private int stock;
    private BigDecimal price;
    private boolean featured;
    private boolean sale;
    private int salePercentage;
    private Long categoryId;
    private String mainImageUrl;
    private List<String> secondaryImageUrls;
    private List<String> descriptionImageUrls;
    private List<ProductVariantResponse> variants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}