package org.bbqqvv.backendecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "product_description_images")  // Đặt tên bảng là product_description_images
public class ProductDescriptionImage extends ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Tự động tăng id
    @Column(name = "id")  // Cột id
    private Long id;

    @ManyToOne  // Nhiều ảnh mô tả có thể liên kết với một sản phẩm
    @JoinColumn(name = "product_id", nullable = true)  // Liên kết với bảng Product, cho phép null
    private Product product;

    @Column(name = "image_url", nullable = false)  // Cột chứa URL của ảnh mô tả
    private String imageUrl;

    @Column(name = "created_at", updatable = false)  // Thêm thời gian tạo ảnh
    private LocalDateTime createdAt;

    @PrePersist  // Thêm thời gian khi bản ghi được thêm
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public ProductDescriptionImage(String imageUrl, Product product) {
        this.imageUrl = imageUrl;
        this.product = product;
    }
}
