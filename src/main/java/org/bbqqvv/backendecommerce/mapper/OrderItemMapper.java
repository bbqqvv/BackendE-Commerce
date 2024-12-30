package org.bbqqvv.backendecommerce.mapper;

import jakarta.persistence.*;
import org.bbqqvv.backendecommerce.dto.request.CartItemRequest;
import org.bbqqvv.backendecommerce.dto.request.OrderItemRequest;
import org.bbqqvv.backendecommerce.dto.response.CartItemResponse;
import org.bbqqvv.backendecommerce.dto.response.OrderItemResponse;
import org.bbqqvv.backendecommerce.entity.CartItem;
import org.bbqqvv.backendecommerce.entity.Order;
import org.bbqqvv.backendecommerce.entity.OrderItem;
import org.bbqqvv.backendecommerce.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface OrderItemMapper {
    OrderItem toCartItem(OrderItemRequest orderItemRequest);

//    @Mapping(target = "productId", source = "product.id")
//    @Mapping(target = "productName", source = "product.name")
//    @Mapping(target = "mainImageUrl", source = "product.mainImage.imageUrl", defaultValue = "")
//    @Mapping(target = "productName", source = "product.name")
//    @Mapping(target = "price", source = "product.price")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);



//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "order_id", nullable = false)
//    private Order order;
//
//    @ManyToOne
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
//
//    @Column(nullable = false)
//    private Integer quantity;
//
//    @Column(nullable = false)
//    private BigDecimal price;
//}
}