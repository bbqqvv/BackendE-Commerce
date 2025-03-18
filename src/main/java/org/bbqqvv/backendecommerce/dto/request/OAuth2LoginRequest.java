package org.bbqqvv.backendecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2LoginRequest {
    @NotBlank(message = "Google token is required")
    private String token;
}
