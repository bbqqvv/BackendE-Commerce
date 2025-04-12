package org.bbqqvv.backendecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(length = 200)
    private String shortDescription;

    @Column(length = 5000)
    private String description;

    @Column(nullable = false, unique = true, length = 100)
    private String productCode;

    @Column(nullable = false)
    @Min(0)
    @Max(100)
    private int salePercentage;

    @Column(nullable = true)
    private boolean featured;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = true)
    private boolean isOldProduct = false;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Favourite> favourites;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private ProductMainImage mainImage;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductSecondaryImage> secondaryImages;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDescriptionImage> descriptionImages;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductVariant> variants;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductReview> reviews;
    @ManyToMany
    @JoinTable(
            name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
