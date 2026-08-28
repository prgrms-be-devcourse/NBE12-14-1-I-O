package io.project.domain.order.dto;

import io.project.domain.order.entity.OrderItem;

public record OrderItemResponse(
        int orderItemId,
        String name,
        int quantity,
        int price,
        String imageFilename
) {

    public OrderItemResponse(OrderItem orderItem) {
        this(
                orderItem.getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getProduct().getFileName()
        );
    }
}