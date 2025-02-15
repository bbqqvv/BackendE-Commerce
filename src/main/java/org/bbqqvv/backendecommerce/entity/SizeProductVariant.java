package org.bbqqvv.backendecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variant_size")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SizeProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_product_id", nullable = false)
    private SizeProduct sizeProduct;

    @Column(nullable = false)
    private int stock;
}
