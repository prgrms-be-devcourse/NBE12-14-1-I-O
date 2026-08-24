package io.project.domain.order.entity;

import io.project.domain.OrderItem;
import io.project.domain.delivery.entity.Delivery;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @CreatedDate
    private LocalDateTime orderedAt;

    @ManyToOne // LAZY ? EAGER ?
    private Delivery delivery;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST,CascadeType.REMOVE},orphanRemoval = true)
    private List<OrderItem> orderItemList;

}
