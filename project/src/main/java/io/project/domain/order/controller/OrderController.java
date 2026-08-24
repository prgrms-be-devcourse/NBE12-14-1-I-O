package io.project.domain.order.controller;

import io.project.domain.order.dto.OrderResponse;
import io.project.domain.order.entity.Order;
import io.project.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{email}")  //이메일이 개인정보라 파라미터로 받아야 할까요?
    public List<OrderResponse> orderList(
            @PathVariable String email
    ){

        List<Order> orderList = this.orderService.findAllByEmail(email);
        List<OrderResponse> orderResponseList = orderList.stream()
                .map(OrderResponse::new)
                .toList();

        return orderResponseList;

    }
}
