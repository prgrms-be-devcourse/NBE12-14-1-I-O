package io.project.domain.order.dto;

public record DashBoardResponse(String name, long quantity, long unitPrice, long totalPrice) {
    public DashBoardResponse(String name, long quantity, long unitPrice, long totalPrice) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
}