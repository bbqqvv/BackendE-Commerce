package org.bbqqvv.backendecommerce.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private String recipientName;

    @Column
    private String country;

    @Column
    private String province;

    @Column
    private String district;

    @Column
    private String email;

    @Column
    private String note;

    @Column
    private String commune;

    @Column(name = "address_line")
    private String addressLine;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "is_default")
    private boolean defaultAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getFullAddress() {
        return String.format("%s, %s, %s, %s, %s",
                addressLine != null ? addressLine : "",
                commune != null ? commune : "",
                district != null ? district : "",
                province != null ? province : "",
                country != null ? country : "");
    }



}
