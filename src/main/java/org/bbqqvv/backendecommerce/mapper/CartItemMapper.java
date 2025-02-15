package org.bbqqvv.backendecommerce.mapper;

import org.bbqqvv.backendecommerce.dto.response.CartItemResponse;
import org.bbqqvv.backendecommerce.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(target = "productId", source = "productVariant.product.id")
    @Mapping(target = "productName", source = "productVariant.product.name")
    @Mapping(target = "mainImageUrl", source = "productVariant.product.mainImage.imageUrl", defaultValue = "")
    @Mapping(target = "color", source = "productVariant.color")
    @Mapping(target = "inStock", source = "inStock")
    CartItemResponse toCartItemResponse(CartItem cartItem);
}
