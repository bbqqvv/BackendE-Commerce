package org.bbqqvv.backendecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "recently_viewed_products",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentlyViewedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDateTime viewedAt;

    @PrePersist
    protected void onCreate() {
        this.viewedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.viewedAt = LocalDateTime.now();
    }
}
