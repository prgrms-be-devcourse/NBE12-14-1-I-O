package io.project.domain.order.dto;

import io.project.domain.order.entity.OrderItem;


public record OrderItemResponse(
        String name,
        int quantity,
        int price,
        String imageFilename
) {
    public OrderItemResponse(OrderItem orderItem){
        this(
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getProduct().getFileName()
        );

    }
}
