package org.bbqqvv.backendecommerce.dto.request;

import lombok.*;
import org.bbqqvv.backendecommerce.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {
    private User user;
    private String country;
    private String province;
    private String district;
    private String commune;
    private String addressLine;
    private String phoneNumber;
    private boolean isDefault;
}
