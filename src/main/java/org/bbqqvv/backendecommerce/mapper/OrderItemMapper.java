package org.bbqqvv.backendecommerce.mapper;

import org.bbqqvv.backendecommerce.dto.request.OrderItemRequest;
import org.bbqqvv.backendecommerce.dto.response.OrderItemResponse;
import org.bbqqvv.backendecommerce.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface OrderItemMapper {
    OrderItem toCartItem(OrderItemRequest orderItemRequest);
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "mainImageUrl", source = "product.mainImage.imageUrl", defaultValue = "")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}


