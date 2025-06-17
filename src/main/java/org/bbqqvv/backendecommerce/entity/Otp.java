package org.bbqqvv.backendecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String otp;
    private LocalDateTime expiryTime; // Hết hạn sau 5 phút

    public Otp(String email, String otp, LocalDateTime localDateTime) {
        this.email = email;
        this.otp = otp;
        this.expiryTime = localDateTime;
    }


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}
