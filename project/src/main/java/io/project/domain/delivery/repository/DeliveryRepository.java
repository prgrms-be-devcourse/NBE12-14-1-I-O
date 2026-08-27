package io.project.domain.delivery.repository;

import io.project.domain.delivery.entity.Delivery;
import io.project.domain.delivery.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
