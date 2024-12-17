package org.bbqqvv.backendecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_main_images")  // Đặt tên bảng là product_main_images
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMainImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Tự động tăng id
    @Column(name = "id")  // Cột id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)  // Liên kết với bảng Product
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
}
