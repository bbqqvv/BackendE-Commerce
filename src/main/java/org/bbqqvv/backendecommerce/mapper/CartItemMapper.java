package org.bbqqvv.backendecommerce.mapper;

import org.bbqqvv.backendecommerce.dto.request.CartItemRequest;
import org.bbqqvv.backendecommerce.dto.response.CartItemResponse;
import org.bbqqvv.backendecommerce.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface CartItemMapper {

    CartItem toCartItem(CartItemRequest cartItemRequest);
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "mainImageUrl", source = "product.mainImage.imageUrl", defaultValue = "")
    @Mapping(target = "price", source = "product.price")
    CartItemResponse toCartItemResponse(CartItem cartItem);
}
