package io.project.domain.order.dto;

import io.project.domain.OrderItem;
import io.project.domain.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        int orderId,
        LocalDateTime orderedAt,
        String status,
        int totalPrice,
        String address,
        int postalCode,
        List<OrderItem> orderItemList          // 상품id, 주문수량, 상품가격 리스트
        // 할일 = 상품 id를 통해 상품 이름과 이미지 받기
){
    public OrderResponse(Order order){
        this(
                order.getId(),
                order.getOrderedAt(),
                order.getDelivery().getStatus(),
                order.getDelivery().getTotalAmount(),
                order.getDelivery().getAddress(),
                order.getDelivery().getPostalCode(),
                order.getOrderItemList()

        );

    }
}
