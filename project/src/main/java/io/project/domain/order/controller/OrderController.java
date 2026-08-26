package io.project.domain.order.controller;

import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.entity.Order;
import io.project.domain.order.service.OrderService;
import io.project.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

/*
1. orderedAt 결정
2. processingDate 계산

3. 요청한 Product들 조회/검증
   - 존재?
   - 수량?
   - 재고?

4. Delivery 조회 or 생성

5. Order 생성

6. OrderItem 생성
7. 재고 차감
8. 저장
 */
    @PostMapping()
    public Order createOrder(
            @Valid @RequestBody OrderCreateRequest request) {

        Order order = orderService.createOrder(request);

        return order;
    }

}
