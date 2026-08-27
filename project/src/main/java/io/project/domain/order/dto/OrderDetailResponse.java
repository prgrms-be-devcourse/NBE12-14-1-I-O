package io.project.domain.order.dto;

import io.project.domain.delivery.entity.DeliveryStatus;
import io.project.domain.order.entity.Order;
import io.project.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        int orderId,
        LocalDateTime orderedAt,
        OrderStatus orderStatus,
        String email,
        String address,
        String postalCode,
        DeliveryStatus deliveryStatus,
        LocalDateTime createdAt,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        List<OrderItemResponse> orderItemResponses
){
    public OrderDetailResponse(Order order){
        this(
                order.getId(),
                order.getOrderedAt(),
                order.getStatus(),
                order.getDelivery().getEmail(),
                order.getDelivery().getAddress(),
                order.getDelivery().getPostalCode(),
                order.getDelivery().getStatus(),
                order.getCreatedAt(),
                order.getDelivery().getShippedAt(),
                order.getDelivery().getDeliveredAt(),
                order.getOrderItems().stream()
                        .map(OrderItemResponse::new)
                        .toList()
        );
    }
}