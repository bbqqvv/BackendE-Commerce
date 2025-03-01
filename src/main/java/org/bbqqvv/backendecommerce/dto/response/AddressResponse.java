package org.bbqqvv.backendecommerce.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private Long userId;
    private String recipientName;
    private String country;
    private String province;
    private String district;
    private String commune;
    private String note;
    private String addressLine;
    private String email;
    private String phoneNumber;
    private boolean defaultAddress;
}
