package io.project.domain.order.controller;

import io.project.domain.order.dto.*;
import io.project.domain.order.entity.Order;
import io.project.domain.order.service.OrderService;
import io.project.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "주문 API",
        description = "고객 주문 생성, 조회, 수정, 취소"
)
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 목록 조회
    @Operation(
            summary = "주문 검색",
            description = "이메일과 조회 기간을 기준으로 고객의 주문 내역을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<RsData<List<OrderListResponse>>> orderList(
            @Valid @ModelAttribute OrderListRequest request
    ) {

        List<OrderListResponse> orderListResponseList =
                orderService.orderList(
                        request.email(),
                        request.startDate(),
                        request.endDate()
                );

        RsData<List<OrderListResponse>> rsData =
                new RsData<>(
                        "200",
                        "주문목록을 성공적으로 불러왔습니다.",
                        orderListResponseList
                );

        return ResponseEntity.ok(rsData);
    }


    // 주문 상세 조회
    @Operation(
            summary = "주문 내역 상세조회",
            description = "주문한 내역의 상세 내용을 확인합니다."
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<RsData<OrderDetailResponse>> orderDetail(
            @PathVariable int orderId
    ) {

        Order order =
                orderService.findById(orderId);

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
    @Operation(
            summary = "새로운 주문 생성",
            description = "상품 정보를 받아 새로운 주문을 생성합니다."
    )
    @PostMapping
    public ResponseEntity<RsData<OrderCreateResponse>> createOrder(
            @Valid @RequestBody OrderCreateRequest request
    ) {

        Order order =
                orderService.createOrder(request);

        RsData<OrderCreateResponse> response =
                new RsData<>(
                        "201-1",
                        "주문이 생성되었습니다.",
                        new OrderCreateResponse(
                                order.getId(),
                                order.getOrderedAt()
                        )
                );

        return ResponseEntity.ok(response);
    }


    // 주문 수정
    @Operation(
            summary = "주문 수정",
            description = "주문의 주소와 우편번호를 수정합니다."
    )
    @PatchMapping("/{orderId}")
    public ResponseEntity<Void> updateOrder(
            @PathVariable int orderId,
            @RequestParam String address,
            @RequestParam String postalCode
    ) {

        orderService.updateOrder(
                orderId,
                address,
                postalCode
        );

        return ResponseEntity.noContent().build();
    }


    // 주문 취소
    @Operation(
            summary = "주문 취소",
            description = "결제한 주문을 취소합니다."
    )
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable int orderId
    ) {

        orderService.cancelOrder(orderId);

        return ResponseEntity.noContent().build();
    }
}