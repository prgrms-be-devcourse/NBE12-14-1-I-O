package io.project.domain.order.controller;
import io.project.domain.order.dto.*;

import java.time.LocalDate;
import java.util.List;
import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderCreateResponse;
import io.project.domain.order.entity.Order;
import io.project.domain.order.service.OrderService;
import io.project.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "주문 API", description = "고객 주문 생성, 조회, 장바구니 담기, 취소")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "주문 검색",
            description = "이메일로 검색해서 고객의 주문 내역을 조회합니다."
    )
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

    @Operation(
            summary = "주문 내역 상세조회",
            description = "주문한 내역의 상세 내용을 확인합니다."
    )

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

    @Operation(
            summary = "새로운 주문 생성",
            description = "상품 정보를 받아 새로운 주문을 생성합니다."
    )

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

    @GetMapping("/dashboard")
    public ResponseEntity<RsData<List<DashBoardResponse>>> dashBoard(
            @RequestParam("startDate")LocalDate startDate,
            @RequestParam("endDate")LocalDate endDate
            ) {
        List<DashBoardResponse> dashBoard = orderService.getDashBoard(startDate, endDate);

        return ResponseEntity.ok(new RsData<>(
                "200",
                "대시보드를 성공적으로 불러왔습니다.",
                dashBoard
        ));
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
