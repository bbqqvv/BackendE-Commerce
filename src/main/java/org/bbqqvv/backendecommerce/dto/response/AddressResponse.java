package org.bbqqvv.backendecommerce.dto.response;

import lombok.*;
import org.bbqqvv.backendecommerce.entity.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private Long userId;
    private String country;
    private String province;
    private String district;
    private String commune;
    private String addressLine;
    private String phoneNumber;
    private boolean isDefault;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
