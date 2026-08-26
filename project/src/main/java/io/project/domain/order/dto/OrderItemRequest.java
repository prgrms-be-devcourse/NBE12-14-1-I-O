package io.project.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull(message = "상품을 선택해주세요")
        Integer productId,

        @NotNull(message = "수량을 입력해주세요.")
        @Min(value = 1, message = "주문 개수는 1개 이상이어야 합니다.")
        Integer quantity
){}