package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.request.OrderRequest;
import org.bbqqvv.backendecommerce.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getOrdersByUser();
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrder(Long orderId, OrderRequest orderRequest);
    OrderResponse updateOrderStatus(Long orderId, String status);
    void cancelOrder(Long orderId);
    void deleteOrder(Long orderId);
}
