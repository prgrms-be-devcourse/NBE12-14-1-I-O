package io.project.domain.order.service;

import io.project.domain.delivery.entity.Delivery;
import io.project.domain.delivery.entity.DeliveryStatus;
import io.project.domain.delivery.repository.DeliveryRepository;
import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderItemRequest;
import io.project.domain.order.dto.OrderListResponse;
import io.project.domain.order.dto.OrderResponse;
import io.project.domain.order.dto.OrderUpdateRequest;
import io.project.domain.order.entity.Order;
import io.project.domain.order.entity.OrderItem;
import io.project.domain.order.entity.OrderStatus;
import io.project.domain.order.repository.OrderRepository;
import io.project.domain.product.entity.Product;
import io.project.domain.product.service.ProductService;
import io.project.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final ProductService productService;

    // 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderListResponse> findAllByEmail(String email) {

        List<Order> orderList =
                orderRepository.findAllByDeliveryEmail(email);

        return orderList.stream()
                .map(OrderListResponse::new)
                .toList();
    }

    // 주문 상세 조회
    @Transactional(readOnly = true)
    public Order findById(int orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "주문을 찾을 수 없습니다."
                        )
                );
    }

    // 주문 생성
    @Transactional
    public Order createOrder(OrderCreateRequest request) {

        LocalDateTime orderedAt = LocalDateTime.now();

        LocalDate processingDate =
                calculateProcessingDate(orderedAt);

        Optional<Delivery> optionalDelivery =
                deliveryRepository
                        .findByEmailAndAddressAndPostalCodeAndProcessingDate(
                                request.email(),
                                request.address(),
                                request.postalCode(),
                                processingDate
                        );

        Delivery delivery;

        if (optionalDelivery.isPresent()) {

            delivery = optionalDelivery.get();

        } else {

            delivery = new Delivery(
                    request.email(),
                    request.address(),
                    request.postalCode(),
                    processingDate
            );

            deliveryRepository.save(delivery);
        }

        Order order =
                new Order(orderedAt, delivery);

        delivery.addOrder(order);

        for (OrderItemRequest itemRequest : request.items()) {

            Product product =
                    productService.decreaseStockForOrder(
                            itemRequest.productId(),
                            itemRequest.quantity()
                    );

            OrderItem orderItem =
                    new OrderItem(
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

    // 주문 수정
    @Transactional
    public OrderResponse updateOrder(
            int orderId,
            OrderUpdateRequest request
    ) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 주문입니다."
                                )
                        );

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException(
                    "취소된 주문은 수정할 수 없습니다."
            );
        }

        Map<Integer, Integer> requestedQuantities =
                new HashMap<>();

        for (OrderUpdateRequest.OrderItemUpdateRequest itemRequest
                : request.getOrderItems()) {

            requestedQuantities.put(
                    itemRequest.getOrderItemId(),
                    itemRequest.getQuantity()
            );
        }

        for (OrderItem orderItem : order.getOrderItems()) {

            int orderItemId =
                    orderItem.getId();

            Integer newQuantity =
                    requestedQuantities.get(orderItemId);

            if (newQuantity == null) {
                continue;
            }

            int oldQuantity =
                    orderItem.getQuantity();

            int quantityDifference =
                    newQuantity - oldQuantity;

            // 주문 수량 증가
            if (quantityDifference > 0) {

                orderItem.getProduct()
                        .removeStock(quantityDifference);

                // 주문 수량 감소
            } else if (quantityDifference < 0) {

                orderItem.getProduct()
                        .addStock(-quantityDifference);
            }

            orderItem.updateQuantity(newQuantity);
        }

        return new OrderResponse(order);
    }

    // 주문 취소
    @Transactional
    public OrderResponse cancelOrder(int orderId) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 주문입니다."
                                )
                        );

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException(
                    "이미 취소된 주문입니다."
            );
        }

        for (OrderItem orderItem : order.getOrderItems()) {

            orderItem.getProduct()
                    .addStock(
                            orderItem.getQuantity()
                    );
        }

        order.cancel();

        return new OrderResponse(order);
    }

    // 배송 처리 날짜 계산
    private LocalDate calculateProcessingDate(
            LocalDateTime orderedAt
    ) {

        LocalTime cutoff =
                LocalTime.of(14, 0);

        if (orderedAt.toLocalTime().isBefore(cutoff)) {

            return orderedAt.toLocalDate();
        }

        return orderedAt
                .toLocalDate()
                .plusDays(1);
    }

    // 배송 시작 처리
    @Transactional
    public void ship() {

        List<Delivery> deliveries =
                deliveryRepository
                        .findAllByStatusAndProcessingDate(
                                DeliveryStatus.ORDERED,
                                LocalDate.now()
                        );

        deliveries.forEach(
                Delivery::updateShipped
        );
    }
}