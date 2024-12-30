package org.bbqqvv.backendecommerce.dto.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String shortDescription;
    private String description;
    private String productCode;
    private int stock;
    private BigDecimal price;
    private boolean featured;
    private boolean sale;
    private int salePercentage;
    private Long categoryId;
    private MultipartFile mainImageUrl;
    private List<MultipartFile> secondaryImageUrls;
    private List<MultipartFile> descriptionImageUrls;
    private List<ProductVariantRequest> variants;

}