package io.project.domain.order.service;

import io.project.domain.delivery.entity.Delivery;
import io.project.domain.delivery.entity.DeliveryStatus;
import io.project.domain.delivery.repository.DeliveryRepository;
import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderItemRequest;
import io.project.domain.order.dto.OrderListGroupResponse;
import io.project.domain.order.dto.OrderListResponse;
import io.project.domain.order.entity.Order;
import io.project.domain.order.entity.OrderItem;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public OrderListGroupResponse findAllByEmail(String email) {
        List<Order> orderList = this.orderRepository.findAllByDeliveryEmail(email);

        //배송 상태별 그룹화

        List<OrderListResponse> orderedList = orderList.stream()
                .filter(order -> order.getDelivery().getStatus() == DeliveryStatus.ORDERED)
                .map(OrderListResponse::new)
                .toList();
        List<OrderListResponse> shippingList = orderList.stream()
                .filter(order -> order.getDelivery().getStatus() == DeliveryStatus.SHIPPING)
                .map(OrderListResponse::new)
                .toList();
        List<OrderListResponse> deliveredList = orderList.stream()
                .filter(order -> order.getDelivery().getStatus() == DeliveryStatus.DELIVERED)
                .map(OrderListResponse::new)
                .toList();
        List<OrderListResponse> cancelledList = orderList.stream()
                .filter(order -> order.getDelivery().getStatus() == DeliveryStatus.CANCELLED)
                .map(OrderListResponse::new)
                .toList();
        int orderedTotalPrice = orderedList.stream()
                .mapToInt(item -> item.totalPrice())
                .sum();
        int shippingTotalPrice = shippingList.stream()
                .mapToInt(item -> item.totalPrice())
                .sum();

        return new OrderListGroupResponse(orderedList,shippingList,deliveredList,cancelledList,
                orderedTotalPrice,shippingTotalPrice);
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

            Product product = productService.decreaseStockForOrder(itemRequest.productId(), itemRequest.quantity());

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
  
    @Transactional(readOnly = true)
    public Order findById(int orderId) {
        return this.orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
    }
}
