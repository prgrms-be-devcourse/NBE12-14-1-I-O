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
}