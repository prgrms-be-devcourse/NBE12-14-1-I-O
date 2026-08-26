package io.project.domain.order.dto;

import java.time.LocalDateTime;

public record OrderCreateResponse (
        int orderId,
        int deliveryId,
        LocalDateTime orderedAt
)
{}
