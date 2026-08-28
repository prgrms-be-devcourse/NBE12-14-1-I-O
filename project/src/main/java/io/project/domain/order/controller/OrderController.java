package io.project.domain.order.controller;

import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderCreateResponse;
import io.project.domain.order.dto.OrderDetailResponse;
import io.project.domain.order.dto.OrderListResponse;
import io.project.domain.order.entity.Order;
import io.project.domain.order.service.OrderService;
import io.project.global.dto.RsData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 목록 조회
    @GetMapping
    public ResponseEntity<RsData<List<OrderListResponse>>> orderList(
            @RequestParam(value = "email") String email
    ) {

        List<OrderListResponse> orderListResponseList =
                this.orderService.findAllByEmail(email);

        if (orderListResponseList.isEmpty()) {
            RsData<List<OrderListResponse>> rsData =
                    new RsData<>(
                            "200",
                            "주문내역이 없습니다.",
                            orderListResponseList
                    );

            return ResponseEntity.ok(rsData);
        }

        RsData<List<OrderListResponse>> rsData =
                new RsData<>(
                        "200",
                        "주문목록을 성공적으로 불러왔습니다.",
                        orderListResponseList
                );

        return ResponseEntity.ok(rsData);
    }

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<RsData<OrderDetailResponse>> orderDetail(
            @PathVariable int orderId
    ) {

        Order order = orderService.findById(orderId);

        OrderDetailResponse orderDetailResponse =
                new OrderDetailResponse(order);

        RsData<OrderDetailResponse> rsData =
                new RsData<>(
                        "200",
                        "상세조회를 성공적으로 불러왔습니다.",
                        orderDetailResponse
                );

        return ResponseEntity.ok(rsData);
    }

    // 주문 생성
    @PostMapping
    public ResponseEntity<RsData<OrderCreateResponse>> createOrder(
            @Valid @RequestBody OrderCreateRequest request
    ) {

        Order order = orderService.createOrder(request);

        RsData<OrderCreateResponse> response =
                new RsData<>(
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

    // 주문 수정
    @PatchMapping("/{orderId}")
    public ResponseEntity<Void> updateOrder(
            @PathVariable int orderId,
            @RequestParam int orderItemId,
            @RequestParam int quantity
    ) {

        orderService.updateOrder(
                orderId,
                orderItemId,
                quantity
        );

        return ResponseEntity.noContent().build();
    }

    // 주문 취소
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable int orderId
    ) {

        orderService.cancelOrder(orderId);

        return ResponseEntity.noContent().build();
    }
}

