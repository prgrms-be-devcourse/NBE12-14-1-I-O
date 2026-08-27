package io.project.global.scheduling;

import io.project.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeliveryScheduler {

    private final OrderService orderService;

    @Scheduled(cron = "0 0 14 * * *")
    public void updateShip() {
        orderService.ship();
    }
}
