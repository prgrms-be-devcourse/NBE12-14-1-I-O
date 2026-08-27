package io.project.domain.order.service;

import io.project.domain.delivery.entity.Delivery;
import io.project.domain.delivery.entity.DeliveryStatus;
import io.project.domain.delivery.repository.DeliveryRepository;
import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderItemRequest;
import io.project.domain.order.dto.OrderListResponse;
import io.project.domain.order.entity.Order;
import io.project.domain.order.entity.OrderItem;
import io.project.domain.order.repository.OrderRepository;
import io.project.domain.product.entity.Product;
import io.project.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<OrderListResponse> findAllByEmail(String email) {
        List<Order> orderList = this.orderRepository.findAllByDeliveryEmail(email);
        List<OrderListResponse> orderListResponseList = orderList.stream()
                .map(OrderListResponse::new)
                .toList();

        return orderListResponseList;
    }
 

    @Transactional
    public Order createOrder(OrderCreateRequest request) {
        // 이 주문이 들어온 서버 시간
        LocalDateTime orderedAt = LocalDateTime.now();
        // 이 주문이 어느 배송 처리 그룹인지 계산
        LocalDate processingDate = calculateProcessingDate(orderedAt);

        // 같은 배송 그룹이 이미 있는지 조회
        Optional<Delivery> optionalDelivery = deliveryRepository.findByEmailAndAddressAndPostalCodeAndProcessingDate(
                request.email(),
                request.address(),
                request.postalCode(),
                processingDate
        );

        Delivery delivery;

        if(optionalDelivery.isPresent()) {
            // 있으면 기존 배송 그룹 사용
            delivery = optionalDelivery.get();
        }
        else{
            // 없으면 새로운 배송 그룹 생성
            delivery = new Delivery(
                    request.email(),
                    request.address(),
                    request.postalCode(),
                    processingDate
            );
            deliveryRepository.save(delivery);
        }

        Order order = new Order(orderedAt, delivery);
        delivery.addOrder(order);

        for (OrderItemRequest itemRequest : request.items()) {

            Product product = productService.findAndRemoveStock(itemRequest.productId(), itemRequest.quantity());

            OrderItem orderItem = new OrderItem(
                    order,
                    product,
                    itemRequest.quantity(),
                    product.getPrice()
            );

            order.addOrderItem(orderItem);
        }

        orderRepository.save(order);

        return order;
    }

    private LocalDate calculateProcessingDate(LocalDateTime orderedAt) {
        LocalTime cutoff = LocalTime.of(14, 0);

        if (orderedAt.toLocalTime().isBefore(cutoff)) {
            return orderedAt.toLocalDate();
        }

        return orderedAt.toLocalDate().plusDays(1);
    }

    @Transactional
    public void ship() {
        List<Delivery> deliveries = deliveryRepository.findAllByStatusAndProcessingDate(
                        DeliveryStatus.ORDERED, LocalDate.now());

        deliveries.forEach(Delivery::updateShipped);
    }
}
