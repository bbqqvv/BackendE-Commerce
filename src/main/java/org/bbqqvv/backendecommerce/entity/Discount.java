package org.bbqqvv.backendecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // Mã giảm giá

    @Column(nullable = false)
    private BigDecimal discountAmount; // Số tiền giảm

    @Column(nullable = false)
    private boolean isPercentage; // true: %, false: VNĐ

    @Column(nullable = false)
    private LocalDateTime expiryDate; // Ngày hết hạn

    private boolean isActive; // Mã có đang hoạt động không?
}
