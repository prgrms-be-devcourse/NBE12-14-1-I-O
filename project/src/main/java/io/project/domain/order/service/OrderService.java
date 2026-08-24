package io.project.domain.order.service;

import io.project.domain.order.entity.Order;
import io.project.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> findAllByEmail(String email) {
        List<Order> orderList = this.orderRepository.findAll()
                .stream()
                .filter(o ->o.getDelivery().getEmail().equals(email))
                .toList();
        return orderList;
    }
}
