package org.bbqqvv.backendecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity@Table(name = "product_secondary_images")  // Đặt tên bảng là product_secondary_images
public class ProductSecondaryImage extends ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Tự động tăng id
    @Column(name = "id")  // Cột id
    private Long id;

    @ManyToOne  // Nhiều ảnh phụ có thể liên kết với một sản phẩm
    @JoinColumn(name = "product_id", nullable = false)  // Liên kết với bảng Product
    private Product product;

    @Column(name = "image_url", nullable = false)  // Cột chứa URL của ảnh phụ
    private String imageUrl;

    @Column(name = "created_at", updatable = false)  // Thêm thời gian tạo ảnh
    private LocalDateTime createdAt;

    @PrePersist  // Thêm thời gian khi bản ghi được thêm
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
    public ProductSecondaryImage(String imageUrl, Product product) {
        this.imageUrl = imageUrl;
        this.product = product;
    }
}
