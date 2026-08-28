package io.project.domain.product;

import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderItemRequest;
import io.project.domain.order.dto.OrderListResponse;
import io.project.domain.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderListTest {

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("주문 목록 조회")
    void orderList() {

        // given
        String email = "test@test.com";

        OrderCreateRequest request1 = new OrderCreateRequest(
                email,
                "서울시 강남구 테헤란로 123",
                "06234",
                List.of(
                        new OrderItemRequest(1, 2)
                )
        );

        OrderCreateRequest request2 = new OrderCreateRequest(
                email,
                "서울시 강남구 역삼동 456",
                "06235",
                List.of(
                        new OrderItemRequest(2, 1),
                        new OrderItemRequest(3, 2)
                )
        );

        OrderCreateRequest request3 = new OrderCreateRequest(
                email,
                "서울시 서초구 서초대로 789",
                "06500",
                List.of(
                        new OrderItemRequest(1, 1),
                        new OrderItemRequest(3, 3)
                )
        );

        // 주문 생성
        orderService.createOrder(request1);
        orderService.createOrder(request2);
        orderService.createOrder(request3);

        // when
        List<OrderListResponse> result =
                orderService.findAllByEmail(email);

        // then
        assertThat(result).hasSize(3);
    }
}