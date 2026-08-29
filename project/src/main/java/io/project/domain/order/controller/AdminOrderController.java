package io.project.domain.order.controller;

import io.project.domain.order.dto.DashBoardResponse;
import io.project.domain.order.service.OrderService;
import io.project.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
}
