package io.project.domain.order.controller;

import io.project.domain.order.dto.OrderListResponse;
import io.project.domain.order.service.OrderService;
import io.project.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<RsData<List<OrderListResponse>>> orderList(
            @RequestParam(value = "email") String email
    ){

        List<OrderListResponse> orderListResponseList = this.orderService.findAllByEmail(email);

        if(orderListResponseList.isEmpty()){
            RsData<List<OrderListResponse>> rsData = new RsData<>("200","주문내역이 없습니다.",null);
            return ResponseEntity.ok(rsData);
        }

        RsData<List<OrderListResponse>> rsData = new RsData<>("200","",orderListResponseList);
        return ResponseEntity.ok(rsData);
    }
}
