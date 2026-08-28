package io.project.domain.delivery.repository;

import io.project.domain.delivery.entity.Delivery;
import io.project.domain.delivery.entity.DeliveryStatus;
import io.project.domain.order.dto.DashBoardResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
    Optional<Delivery> findByEmailAndAddressAndPostalCodeAndProcessingDate(
            String email,
            String address,
            String postalCode,
            LocalDate processingDate
    );

    List<Delivery> findAllByStatusAndProcessingDate(DeliveryStatus status, LocalDate processingDate);

    List<Delivery> findAllByStatusIsNotAndProcessingDate(DeliveryStatus status, LocalDate processingDate);

    @Query("select p.name, sum(oi.quantity), sum(oi.unitPrice), sum(oi.quantity * oi.unitPrice) " +
            "from Delivery d " +
            "join Order o on d.id = o.delivery.id " +
            "join OrderItem oi on o.id = oi.order.id " +
            "join Product p on oi.product.id = p.id " +
            "where d.status != DeliveryStatus.CANCELLED " +
            "AND CAST(d.createdAt as date) >= :startDate " +
            "AND CAST(d.createdAt as date) <= :endDate " +
            "group by p.name")
    List<DashBoardResponse> findDashBoard(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

}
