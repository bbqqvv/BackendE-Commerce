package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.PageResponse;
import org.bbqqvv.backendecommerce.dto.request.OrderRequest;
import org.bbqqvv.backendecommerce.dto.response.OrderResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    PageResponse<OrderResponse> getOrdersByUser(Pageable pageable);
    PageResponse<OrderResponse> getAllOrders(Pageable pageable);
    OrderResponse updateOrder(Long orderId, OrderRequest orderRequest);
    OrderResponse updateOrderStatus(Long orderId, String status);
    void cancelOrder(Long orderId);
    void deleteOrder(Long orderId);
    OrderResponse getOrderByCode(String orderCode);

    boolean isProductDeliveredToCurrentUser(Long productId);
}
