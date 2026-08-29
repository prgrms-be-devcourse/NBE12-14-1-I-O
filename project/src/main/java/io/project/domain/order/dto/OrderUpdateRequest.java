package io.project.domain.order.dto;

public record OrderUpdateRequest(
        int orderItemId,
        int quantity,
        String address,
        String postalCode
) {
}