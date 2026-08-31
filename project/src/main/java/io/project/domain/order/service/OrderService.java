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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
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

            if (delivery.getStatus() == DeliveryStatus.CANCELLED) {
                delivery.reopen();
            }

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

        // 1. 수정할 주문 조회
        Order order = findById(orderId);

        // 2. 이미 취소된 주문인지 확인
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException(
                    "취소된 주문은 수정할 수 없습니다."
            );
        }

        // 3. 현재 주문이 속한 배송 그룹
        Delivery delivery = order.getDelivery();

        // 4. 배송 처리일 기준 수정 가능 시간 계산
        LocalDate processingDate =
                delivery.getProcessingDate();

        LocalDateTime updateDeadline =
                processingDate.atTime(14, 0);

        LocalDateTime now =
                LocalDateTime.now(clock);

        // 5. 배송 처리일 오후 2시 이후에는 수정 불가
        if (!now.isBefore(updateDeadline)) {
            throw new IllegalStateException(
                    "배송 처리일 오후 2시 이후에는 주문을 수정할 수 없습니다."
            );
        }

        // 6. 이미 배송이 시작된 주문은 수정 불가
        if (delivery.getStatus() != DeliveryStatus.ORDERED) {
            throw new IllegalStateException(
                    "배송이 시작된 주문은 수정할 수 없습니다."
            );
        }

        // 7. 요청값 검증
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

        // 8. 기존 배송지와 같다면 수정할 필요 없음
        if (delivery.getAddress().equals(address)
                && delivery.getPostalCode().equals(postalCode)) {
            return;
        }

        // 9. 변경하려는 배송 조건과 동일한 Delivery 조회
        Optional<Delivery> optionalNewDelivery =
                deliveryRepository
                        .findByEmailAndAddressAndPostalCodeAndProcessingDate(
                                delivery.getEmail(),
                                address,
                                postalCode,
                                processingDate
                        );

        Delivery newDelivery;

        if (optionalNewDelivery.isPresent()) {

            // 10-1. 기존 Delivery 재사용
            newDelivery = optionalNewDelivery.get();

            // 기존 배송 그룹이 취소 상태라면 다시 활성화
            if (newDelivery.getStatus() == DeliveryStatus.CANCELLED) {
                newDelivery.reopen();
            }

            // 방어 코드:
            // 취소 상태는 reopen으로 ORDERED가 되지만,
            // SHIPPING 등 이미 배송이 시작된 그룹에는 합칠 수 없음
            if (newDelivery.getStatus() != DeliveryStatus.ORDERED) {
                throw new IllegalStateException(
                        "이미 배송이 시작된 배송 그룹으로 변경할 수 없습니다."
                );
            }

        } else {

            // 10-2. 같은 조건의 Delivery가 없다면 새로 생성
            newDelivery = new Delivery(
                    delivery.getEmail(),
                    address,
                    postalCode,
                    processingDate
            );

            deliveryRepository.save(newDelivery);
        }

        // 11. 기존 배송 그룹에서 현재 주문 제거
        delivery.removeOrder(order);

        // 12. Order의 실제 Delivery 참조 변경
        order.changeDelivery(newDelivery);

        // 13. 새로운 배송 그룹의 주문 목록에도 추가
        newDelivery.addOrder(order);

        // 14. 기존 Delivery에 주문이 하나도 남지 않았다면 취소
        if (delivery.getOrders().isEmpty()) {
            delivery.cancel();
        }
    }

    @Transactional
    public void cancelOrder(int orderId) {

        Order order = findById(orderId);

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException(
                    "이미 취소된 주문입니다."
            );
        }

        Delivery delivery = order.getDelivery();

        if (delivery.getStatus() != DeliveryStatus.ORDERED) {
            throw new IllegalStateException(
                    "배송이 시작된 주문은 취소할 수 없습니다."
            );
        }

        for (OrderItem orderItem : order.getOrderItems()) {

            productService.increaseStockForCancel(
                    orderItem.getProduct().getId(),
                    orderItem.getQuantity()
            );
        }

        order.cancel();

        boolean allCanceled =
                delivery.getOrders()
                        .stream()
                        .allMatch(
                                deliveryOrder ->
                                        deliveryOrder.getStatus()
                                                == OrderStatus.CANCELED
                        );

        if (allCanceled) {
            delivery.cancel();
        }
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

        LocalDateTime now = LocalDateTime.now(clock);

        List<Delivery> deliveries =
                deliveryRepository
                        .findAllByStatusAndProcessingDate(
                                DeliveryStatus.ORDERED,
                                now.toLocalDate()
                        );

        deliveries.forEach(
                delivery -> delivery.updateShipped(now)
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

    @Transactional(readOnly = true)
    public Page<OrderListResponse> orderListPaging(String name, LocalDate startDate, LocalDate endDate,
                                String status, int page, int size, String sort) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (status == null || status.isEmpty()) {
            status = "ORDERED";
        }
        DeliveryStatus deliveryStatus = DeliveryStatus.valueOf(status);

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.valueOf(sort.toUpperCase()), "createdAt"));

        Page<Order> orderPage = orderRepository.findAdminOrderList(name, startDate, endDate, deliveryStatus, pageable);
        return orderPage.map(OrderListResponse::new);
    }
}