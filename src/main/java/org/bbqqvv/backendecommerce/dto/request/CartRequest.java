package org.bbqqvv.backendecommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CartRequest {
    @NotNull(message = "Cart items cannot be null")
    private List<CartItemRequest> items;
}
