package io.project.domain.order.controller;

import io.project.domain.order.dto.OrderListResponse;

import java.util.List;
import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderCreateResponse;
import io.project.domain.order.entity.Order;
import io.project.domain.order.service.OrderService;
import io.project.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
