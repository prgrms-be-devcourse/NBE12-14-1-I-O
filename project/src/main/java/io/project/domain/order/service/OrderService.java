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

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static io.project.domain.delivery.repository.DeliveryRepository.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final ProductService productService;
    private final Clock clock;

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

        LocalDateTime orderedAt = LocalDateTime.now(clock);

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
                new Order(
                        orderedAt,
                        delivery
                );

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
            String address,
            String postalCode
    ) {

        Order order = findById(orderId);

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException(
                    "취소된 주문은 수정할 수 없습니다."
            );
        }

        Delivery delivery =
                order.getDelivery();

        LocalDate processingDate =
                delivery.getProcessingDate();

        LocalDateTime updateDeadline =
                processingDate.atTime(14, 0);

        LocalDateTime now =
                LocalDateTime.now();

        if (!now.isBefore(updateDeadline)) {
            throw new IllegalStateException(
                    "배송 처리일 오후 2시 이후에는 주문을 수정할 수 없습니다."
            );
        }

        if (delivery.getStatus() != DeliveryStatus.ORDERED) {
            throw new IllegalStateException(
                    "배송이 시작된 주문은 수정할 수 없습니다."
            );
        }

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException(
                    "주소를 입력해주세요."
            );
        }

        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException(
                    "우편번호를 입력해주세요."
            );
        }

        delivery.updateAddress(
                address,
                postalCode
        );
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

    // 관리자 대시보드
    public DashBoardResponse getDashBoard(
            LocalDate startDate,
            LocalDate endDate
    ) {

        List<RevenueDashBoard> revenueDashBoards =
                deliveryRepository.findDashBoard(
                        startDate,
                        endDate
                );

        List<SoldTop3DashBoard> soldTop3DashBoards =
                deliveryRepository.findSoldTop3DashBoard(
                        startDate,
                        endDate
                );

        List<RevenueTop3DashBoard> revenueTop3DashBoards =
                deliveryRepository.findRevenueTop3DashBoard(
                        startDate,
                        endDate
                );

        return new DashBoardResponse(
                revenueDashBoards,
                soldTop3DashBoards,
                revenueTop3DashBoards
        );
    }

    // 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderListResponse> orderList(
            String email,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null && endDate != null) {
            throw new InvalidException(
                    "시작일을 입력해주세요."
            );
        }

        if (startDate != null && endDate == null) {
            throw new InvalidException(
                    "종료일을 입력해주세요."
            );
        }

        if (startDate != null
                && endDate != null
                && startDate.isAfter(endDate)) {

            throw new InvalidException(
                    "시작일은 종료일보다 이후일 수 없습니다."
            );
        }

        if (startDate != null && endDate != null) {

            LocalDateTime startDateTime =
                    startDate.atStartOfDay();

            LocalDateTime endDateTime =
                    endDate
                            .plusDays(1)
                            .atStartOfDay();

            List<Order> orderList =
                    orderRepository
                            .findAllByDeliveryEmailAndOrderedAtBetweenOrderByOrderedAtDesc(
                                    email,
                                    startDateTime,
                                    endDateTime
                            );

            return orderList.stream()
                    .map(OrderListResponse::new)
                    .toList();
        }

        List<Order> orderList =
                orderRepository
                        .findAllByDeliveryEmailOrderByOrderedAtDesc(
                                email
                        );

        return orderList.stream()
                .map(OrderListResponse::new)
                .toList();
    }
}