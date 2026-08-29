package io.project.domain.order.service;

import io.project.domain.delivery.repository.DeliveryRepository;
import io.project.domain.order.dto.DashBoardResponse;
import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderItemRequest;
import io.project.domain.order.repository.OrderRepository;
import io.project.domain.product.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static io.project.domain.product.dto.ProductRequest.ProductAddRequest;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    ProductService productService;

    @BeforeEach
    void setUp() {
        ProductAddRequest[] requests = {
                new ProductAddRequest("productA", 100, 100, null),
                new ProductAddRequest("productB", 100, 200, null),
                new ProductAddRequest("productC", 100, 300, null),
                new ProductAddRequest("productD", 100, 10000, null)
        };

        for (ProductAddRequest request : requests) {
            productService.save(request);
        }

    }

    @Test
    @DisplayName("대시보드")
    @Transactional
    void getDashBoard() {
        OrderService orderService = new OrderService(orderRepository, deliveryRepository, productService);

        createOrder(orderService, 1, 2); // 수익 200
        createOrder(orderService, 2, 5); // 수익 1000
        createOrder(orderService, 3, 10); // 수익 3000
        createOrder(orderService, 4, 1); // 수익 10000


        DashBoardResponse dashBoard = orderService.getDashBoard(LocalDate.now(), LocalDate.now());

        System.out.println("dashBoard = " + dashBoard);

        // 총 수익 검증
        assertEquals(14200,
                dashBoard.revenueDashBoards().stream()
                        .mapToInt(d -> (int) d.totalPrice())
                        .sum()
        );

        // 팔린 개수 Top 3 검증
        assertArrayEquals(new String[]{"productC", "productB", "productA"},
                dashBoard.soldTop3DashBoards().stream()
                        .map(d -> d.name()).toArray(String[]::new)
        );

        // 수익 Top 3 검증
        assertArrayEquals(new String[]{"productD", "productC", "productB"},
                dashBoard.revenueTop3DashBoards().stream()
                        .map(d -> d.name()).toArray(String[]::new)
        );
    }

    private void createOrder(OrderService orderService, int productId, int quantity) {
        orderService.createOrder(new OrderCreateRequest(
                "test@test",
                "서울시 강남구",
                "12345",
                List.of(new OrderItemRequest(productId, quantity))));
    }
}