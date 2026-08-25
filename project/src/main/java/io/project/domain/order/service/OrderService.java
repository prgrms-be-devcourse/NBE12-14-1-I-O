package io.project.domain.order.service;

import io.project.domain.order.dto.OrderListResponse;
import io.project.domain.order.entity.Order;
import io.project.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderListResponse> findAllByEmail(String email) {
        List<Order> orderList = this.orderRepository.findAllByDeliveryEmail(email);
        List<OrderListResponse> orderListResponseList = orderList.stream()
                .map(OrderListResponse::new)
                .toList();

        return orderListResponseList;
    }
}
