package io.project.domain.order.repository;

import io.project.domain.delivery.entity.DeliveryStatus;
import io.project.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findAllByDeliveryEmailOrderByOrderedAtDesc(String email);

    List<Order> findAllByDeliveryEmailAndOrderedAtBetweenOrderByOrderedAtDesc(
            String email,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("select o " +
            "from Order o " +
            "join Delivery d on o.delivery.id = d.id " +
            "join OrderItem oi on o.id = oi.order.id " +
            "join Product p on p.id = oi.product.id " +
            "where (:name is null or p.name like %:name%) " +
            "and CAST(d.createdAt as date) >= :startDate " +
            "and CAST(d.createdAt as date) <= :endDate " +
            "and d.status = :status")
    Page<Order> findAdminOrderList(@Param("name") String name,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   @Param("status") DeliveryStatus status,
                                   Pageable pageable);
}