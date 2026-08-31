package io.project.domain.order.controller;

import io.project.domain.order.dto.DashBoardResponse;
import io.project.domain.order.dto.OrderListResponse;
import io.project.domain.order.service.OrderService;
import io.project.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "관리자 주문 API", description = "관리자 주문 조회, 통계")
@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(
            summary = "대시 보드",
            description = "설정한 기간 내의 대시보드를 조회합니다."
    )
    @GetMapping("/dashboard")
    public ResponseEntity<RsData<DashBoardResponse>> dashBoard(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate")LocalDate endDate
    ) {
        DashBoardResponse dashBoard = orderService.getDashBoard(startDate, endDate);

        return ResponseEntity.ok(new RsData<>(
                "200",
                "대시보드를 성공적으로 불러왔습니다.",
                dashBoard
        ));
    }

    @Operation(
            summary = "주문 검색",
            description = "상품명, 기간, 배송 상태, 페이징 필드를 기준으로 고객의 주문 내역을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<RsData<Page<OrderListResponse>>> orderList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false, defaultValue = "desc") String sort
    ) {
        Page<OrderListResponse> response = orderService.orderListPaging(name, startDate, endDate, status, page, size, sort);

        return ResponseEntity.ok(new RsData<>(
                "200",
                "주문 목록을 성공적으로 불러왔습니다.",
                response
        ));
    }
}
