package io.project.domain.order.controller;

import io.project.domain.order.dto.*;

import java.util.List;

import io.project.domain.order.entity.Order;
import io.project.domain.order.service.OrderService;
import io.project.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<RsData<OrderListGroupResponse>> orderList(
            @RequestParam(value = "email") String email
    ){

        OrderListGroupResponse orderListGroupResponse = this.orderService.findAllByEmail(email);

        RsData<OrderListGroupResponse> rsData = new RsData<>("200",
                "주문목록을 성공적으로 불러왔습니다.",
                orderListGroupResponse);
        return ResponseEntity.ok(rsData);
    }

    @GetMapping("{orderId}")
    public ResponseEntity<RsData<OrderDetailResponse>> orderDetail(
            @PathVariable(value = "orderId") int orderId){
        Order order = this.orderService.findById(orderId);
        OrderDetailResponse orderDetailResponse = new OrderDetailResponse(order);
        RsData<OrderDetailResponse> rsData = new RsData<>(
                "200",
                "상세조회를 성공적으로 불러왔습니다.",
                orderDetailResponse);

        return ResponseEntity.ok(rsData);
    }

    @PostMapping()
    public ResponseEntity<RsData<OrderCreateResponse>> createOrder(
            @Valid @RequestBody OrderCreateRequest request) {

        Order order = orderService.createOrder(request);

        RsData<OrderCreateResponse> response = new RsData<>(
                "201-1",
                "주문이 생성되었습니다.",
                new OrderCreateResponse(
                        order.getId(),
                        order.getDelivery().getId(),
                        order.getOrderedAt()
                )
        );

        return ResponseEntity.ok(response);
    }

}