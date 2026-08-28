package io.project.domain.order.service;

import io.project.domain.delivery.entity.Delivery;
import io.project.domain.delivery.entity.DeliveryStatus;
import io.project.domain.delivery.repository.DeliveryRepository;
import io.project.domain.order.dto.DashBoardResponse;
import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderItemRequest;
import io.project.domain.order.dto.OrderListResponse;
import io.project.domain.order.entity.Order;
import io.project.domain.order.entity.OrderItem;
import io.project.domain.order.entity.OrderStatus;
import io.project.domain.order.repository.OrderRepository;
import io.project.domain.product.entity.Product;
import io.project.domain.product.service.ProductService;
import io.project.global.exception.InvalidException;
import io.project.global.exception.NotFoundException;
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


    // 주문 상세 조회
    @Transactional(readOnly = true)
    public Order findById(int orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new NotFoundException("주문을 찾을 수 없습니다.")
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
    public void updateOrder(
            int orderId,
            int orderItemId,
            int quantity
    ) {

        Order order = findById(orderId);

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException(
                    "취소된 주문은 수정할 수 없습니다."
            );
        }

        if (quantity < 1) {
            throw new IllegalArgumentException(
                    "주문 수량은 1개 이상이어야 합니다."
            );
        }

        OrderItem orderItem =
                order.getOrderItems()
                        .stream()
                        .filter(item ->
                                item.getId() == orderItemId
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "주문 상품을 찾을 수 없습니다."
                                )
                        );

        int oldQuantity =
                orderItem.getQuantity();

        int quantityDifference =
                quantity - oldQuantity;

        // 주문 수량 증가
        if (quantityDifference > 0) {

            productService.decreaseStockForOrder(
                    orderItem.getProduct().getId(),
                    quantityDifference
            );

            // 주문 수량 감소
        } else if (quantityDifference < 0) {

            productService.increaseStockForCancel(
                    orderItem.getProduct().getId(),
                    -quantityDifference
            );
        }

        orderItem.updateQuantity(quantity);
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(int orderId) {

        Order order = findById(orderId);

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException(
                    "이미 취소된 주문입니다."
            );
        }

        for (OrderItem orderItem : order.getOrderItems()) {

            productService.increaseStockForCancel(
                    orderItem.getProduct().getId(),
                    orderItem.getQuantity()
            );
        }

        order.cancel();
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

    // 배송 처리
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

    public List<DashBoardResponse> getDashBoard(LocalDate startDate, LocalDate endDate) {
        List<DashBoardResponse> dashBoard = deliveryRepository.findDashBoard(startDate, endDate);
        for (DashBoardResponse dashBoardResponse : dashBoard) {
            System.out.println("dashBoardResponse = " + dashBoardResponse);
        }
        return dashBoard;
    }

    // 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderListResponse> orderList(
            String email,
            LocalDate startDate,
            LocalDate endDate
    ) {
        
        if (startDate == null && endDate != null) {
            throw new InvalidException("시작일을 입력해주세요.");
        }
        if (startDate != null && endDate == null) {
            throw new InvalidException("종료일을 입력해주세요.");
        }


        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
            List<Order> orderList =  this.orderRepository.findAllByDeliveryEmailAndOrderedAtBetween(
                    email,startDateTime,endDateTime);

            return orderList.stream()
                    .map(OrderListResponse::new)
                    .toList();
        }

        List<Order> orderList = this.orderRepository.findAllByDeliveryEmail(email);

        return orderList.stream()
                .map(OrderListResponse::new)
                .toList();
    }
}