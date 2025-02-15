package org.bbqqvv.backendecommerce.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {
    private String country;
    private String province;
    private String district;
    private String commune;
    private String addressLine;
    private String phoneNumber;
    private boolean defaultAddress;
}
