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
@Table(name = "product_main_images")  // Đặt tên bảng là product_main_images
public class ProductMainImage extends ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Tự động tăng id
    @Column(name = "id")  // Cột id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)  // Liên kết với bảng Product (Nhiều ảnh chính có thể thuộc về 1 sản phẩm)
    @JoinColumn(name = "product_id", nullable = false)  // Liên kết với bảng Product
    private Product product;

    @Column(name = "image_url", nullable = false)  // Cột chứa URL của ảnh chính
    private String imageUrl;

    @Column(name = "created_at", updatable = false)  // Thêm thời gian tạo ảnh
    private LocalDateTime createdAt;

    @PrePersist  // Thêm thời gian khi bản ghi được thêm vào CSDL
    public void prePersist() {
        this.createdAt = LocalDateTime.now();  // Lưu thời gian tạo ảnh
    }

    public ProductMainImage(String imageUrl, Product product) {
        this.imageUrl = imageUrl;
        this.product = product;
    }
}
