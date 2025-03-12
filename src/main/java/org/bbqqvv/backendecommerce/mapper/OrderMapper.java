package org.bbqqvv.backendecommerce.mapper;

import org.bbqqvv.backendecommerce.dto.request.OrderRequest;
import org.bbqqvv.backendecommerce.dto.response.OrderResponse;
import org.bbqqvv.backendecommerce.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    Order toOrder(OrderRequest orderRequest);
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "address", source = "fullAddress")
    @Mapping(target = "discountCode", source = "discount.code", defaultValue = "") // Đảm bảo discountCode không null
    @Mapping(target = "discountAmount", source = "discountAmount", defaultValue = "0") // Tránh null
    OrderResponse toCartResponse(Order order);
}


//private Long id;
//private User user;
//private Long addressId;
//private List<OrderItem> orderItems;
//private OrderStatus status = OrderStatus.PENDING;
//private Discount discount;
//private PaymentMethod paymentMethod;
//private String discountCode;
//private BigDecimal discountAmount;
//private BigDecimal shippingFee;
//private LocalDate expectedDeliveryDate;
//private BigDecimal totalAmount;
//private LocalDateTime createdAt;
//private LocalDateTime updatedAt;

//
//private Long id;
//private Long userId;
//private String userName;
//private String address;
//private String status;
//private PaymentMethod paymentMethod;
//private BigDecimal shippingFee;
//private String discountCode;
//private BigDecimal discountAmount;
//private BigDecimal totalAmount;
//private LocalDate expectedDeliveryDate;
//private List<OrderItemResponse> items;
//private LocalDateTime createdAt;
//private LocalDateTime updatedAt;