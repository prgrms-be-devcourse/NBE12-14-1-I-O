package io.project.domain.order.dto;

import io.project.domain.delivery.entity.DeliveryStatus;
import io.project.domain.order.entity.Order;
import io.project.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderListResponse(
        int orderId,
        LocalDateTime orderedAt,
        OrderStatus orderStatus,
        String email,
        DeliveryStatus deliveryStatus,
        String address,
        String postalCode,
        int price,
        List<OrderItemResponse> orderItemResponses
){
    public OrderListResponse(Order order){
        this(
                order.getId(),
                order.getOrderedAt(),
                order.getStatus(),
                order.getDelivery().getEmail(),
                order.getDelivery().getStatus(),
                order.getDelivery().getAddress(),
                order.getDelivery().getPostalCode(),
                order.getOrderItems().stream()
                        .mapToInt(oi -> oi.getUnitPrice() * oi.getQuantity())
                        .sum(),
                order.getOrderItems().stream()
                        .map(OrderItemResponse::new)
                        .toList()
        );
    }
}
