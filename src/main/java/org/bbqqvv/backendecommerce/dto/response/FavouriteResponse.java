package org.bbqqvv.backendecommerce.dto.response;

import lombok.*;
import org.bbqqvv.backendecommerce.entity.Product;
import org.bbqqvv.backendecommerce.entity.User;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class FavouriteResponse {
    private Long id;
    private Long userId;
    private String nameProduct;
    private String imageUrl;
    private String price;
}
