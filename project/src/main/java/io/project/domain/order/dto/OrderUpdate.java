package io.project.domain.order.dto;

import io.project.domain.order.entity.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor

public class OrderUpdateRequest {

    @NotEmpty(message = "수정할 주문 상품이 필요합니다.")
    @Valid
    private List<OrderItemUpdateRequest> orderItems;

    @Getter
    @NoArgsConstructor
    public static class OrderItemUpdateRequest {
        @Positive(message = "주문 상품 ID는 1 이상이어야 합니다.")
        private Integer orderItemId;

        @Positive(message = "주문 수량은 1 이상이어야 합니다.")
        private Integer quantity;
    }
}

@Getter
public class OrderResponse {

    private final int orderId;
    private final  OrderStatus status;
    private final LocalDateTime orderedAt;

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.status = order.getStatus();
        this.orderedAt = order.getOrderedAt();
    }


}