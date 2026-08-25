package io.project.domain.order.entity;

import io.project.domain.delivery.entity.Delivery;
import io.project.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "orders")
@Entity
@Getter
@NoArgsConstructor
public class Order extends BaseEntity {

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.PERSIST
    )
    private List<OrderItem> orderItems = new ArrayList<>();
}