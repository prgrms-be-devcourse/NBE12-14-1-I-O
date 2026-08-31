package io.project.domain.delivery.entity;

import io.project.domain.order.entity.Order;
import io.project.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(
        name = "deliveries",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_delivery_group",
                columnNames = {
                        "email",
                        "address",
                        "postal_code",
                        "processing_date"
                }
        )
)
@Entity
@Getter
@NoArgsConstructor
public class Delivery extends BaseEntity {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String address;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;

    @Column(name = "processing_date", nullable = false)
    private LocalDate processingDate;

    @OneToMany(mappedBy = "delivery")
    private List<Order> orders = new ArrayList<>();

    public Delivery(
            String email,
            String address,
            String postalCode,
            LocalDate processingDate
    ) {
        this.email = email;
        this.address = address;
        this.postalCode = postalCode;
        this.status = DeliveryStatus.ORDERED;
        this.processingDate = processingDate;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    public void removeOrder(Order order) {
        this.orders.remove(order);
    }

    public void updateShipped(LocalDateTime shippedAt) {
        this.status = DeliveryStatus.SHIPPING;
        this.shippedAt = shippedAt;
    }

    // 배송 취소
    public void cancel() {
        this.status = DeliveryStatus.CANCELLED;
    }

    public void reopen() {
        this.status = DeliveryStatus.ORDERED;
    }

}