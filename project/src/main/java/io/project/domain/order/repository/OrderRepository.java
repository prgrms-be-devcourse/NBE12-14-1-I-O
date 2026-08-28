package io.project.domain.order.repository;

import io.project.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findAllByDeliveryEmail(String email);

    List<Order> findAllByDeliveryEmailAndOrderedAtBetween(
            String email,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}