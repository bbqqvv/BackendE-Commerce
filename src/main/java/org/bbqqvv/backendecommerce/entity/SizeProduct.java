package org.bbqqvv.backendecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "size_product")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SizeProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sizeName;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = true)
    private BigDecimal priceAfterDiscount;

    @OneToMany(mappedBy = "sizeProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SizeProductVariant> productVariantSizes; // Sử dụng bảng trung gian
    public int getStockQuantity() {
        return productVariantSizes.stream()
                .mapToInt(SizeProductVariant::getStock)
                .sum();
    }

}