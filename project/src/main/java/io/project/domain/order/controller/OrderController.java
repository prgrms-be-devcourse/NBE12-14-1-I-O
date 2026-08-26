package io.project.domain.order.controller;

import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderCreateResponse;
import io.project.domain.order.dto.OrderDetailResponse;
import io.project.domain.order.dto.OrderListResponse;
import io.project.domain.order.dto.OrderResponse;
import io.project.domain.order.dto.OrderUpdateRequest;
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
            @PathVariable(value = "orderId") int orderId
    ) {

        Order order = this.orderService.findById(orderId);

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
                                order.getId()
                        )
                );

        return ResponseEntity.ok(response);
    }

    // 주문 수정
    @PatchMapping("/{orderId}")
    public ResponseEntity<RsData<OrderResponse>> updateOrder(
            @PathVariable int orderId,
            @Valid @RequestBody OrderUpdateRequest request
    ) {

        OrderResponse response =
                orderService.updateOrder(orderId, request);

        return ResponseEntity.ok(
                new RsData<>(
                        "200",
                        "주문 수정이 완료되었습니다.",
                        response
                )
        );
    }

    // 주문 취소
    @DeleteMapping("/{orderId}")
    public ResponseEntity<RsData<OrderResponse>> cancelOrder(
            @PathVariable int orderId
    ) {

        OrderResponse response =
                orderService.cancelOrder(orderId);

        return ResponseEntity.ok(
                new RsData<>(
                        "200",
                        "주문 취소가 완료되었습니다.",
                        response
                )
        );
    }
}