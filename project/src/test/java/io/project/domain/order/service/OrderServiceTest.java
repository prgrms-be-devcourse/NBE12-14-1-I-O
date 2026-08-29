package io.project.domain.order.service;

import io.project.domain.delivery.repository.DeliveryRepository;
import io.project.domain.order.dto.DashBoardResponse;
import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderItemRequest;
import io.project.domain.order.dto.OrderListResponse;
import io.project.domain.order.entity.Order;
import io.project.domain.order.entity.OrderItem;
import io.project.domain.order.repository.OrderRepository;
import io.project.domain.product.entity.Product;
import io.project.domain.product.service.ProductService;
import io.project.global.exception.InvalidException;
import io.project.global.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static io.project.domain.product.dto.ProductRequest.ProductAddRequest;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    ProductService productService;
    @Autowired
    OrderService orderService;

    Product productA;
    Product productB;
    Product productC;
    Product productD;


    @BeforeEach
    void setUp() {
        productA = productService.save(new ProductAddRequest("productA", 100, 100, null));
        productB = productService.save(new ProductAddRequest("productB", 100, 200, null));
        productC = productService.save(new ProductAddRequest("productC", 100, 300, null));
        productD = productService.save(new ProductAddRequest("productD", 100, 10000, null));
    }

    @Test
    @DisplayName("대시보드")
    @Transactional
    void getDashBoard() {
        OrderService orderService = new OrderService(orderRepository, deliveryRepository, productService);

        createOrder("test@test", 1, 2); // 수익 200
        createOrder("test@test", 2, 5); // 수익 1000
        createOrder("test@test", 3, 10); // 수익 3000
        createOrder("test@test", 4, 1); // 수익 10000


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
    @Test
    @DisplayName("주문 상세 조회")
    @Transactional
    void findById() {
        Order savedOrder =
                createOrder("test@test", productA.getId(), 2);

        Order order = orderService.findById(savedOrder.getId());

        assertEquals(savedOrder.getId(), order.getId());
        assertEquals("test@test", order.getDelivery().getEmail());
        assertEquals("서울시 강남구", order.getDelivery().getAddress());
        assertEquals("12345", order.getDelivery().getPostalCode());
        assertEquals(1, order.getOrderItems().size());

        OrderItem orderItem = order.getOrderItems().get(0);

        assertEquals("productA", orderItem.getProduct().getName());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(100, orderItem.getUnitPrice());
    }


    @Test
    @DisplayName("주문 목록 조회")
    @Transactional
    void orderList() {
        createOrder("test@test", productB.getId(), 2);
        createOrder("test@test", productB.getId(), 5);
        createOrder("test@test", productC.getId(), 10);

        createOrder("other@test", productD.getId(), 1);

        List<OrderListResponse> orderList =
                orderService.orderList("test@test", null, null);

        assertEquals(3, orderList.size());
    }


    @Test
    @DisplayName("주문 목록 조회 - 시작일만 입력")
    void orderListStartDateOnly() {
        assertThrows(
                InvalidException.class, () -> orderService.orderList("test@test", LocalDate.now(), null)
        );
    }


    @Test
    @DisplayName("주문 목록 조회 - 종료일만 입력")
    void orderListEndDateOnly() {
        assertThrows(
                InvalidException.class,
                () -> orderService.orderList("test@test", null, LocalDate.now())
        );
    }


    @Test
    @DisplayName("존재하지 않는 주문 상세 조회")
    void findByIdNotFound() {
        assertThrows(
                NotFoundException.class, () -> orderService.findById(999999));
    }

    @Test
    @DisplayName("주문 목록 조회 - 시작일이 종료일보다 이후인 경우")
    void orderListStartDateAfterEndDate() {
        assertThrows(
                InvalidException.class,
                () -> orderService.orderList("test@test",
                        LocalDate.of(2026, 8, 30),
                        LocalDate.of(2026, 8, 29)
                )
        );
    }



    private Order createOrder(String email, int productId, int quantity) {
        return orderService.createOrder(new OrderCreateRequest(
                email,
                "서울시 강남구",
                "12345",
                List.of(new OrderItemRequest(productId, quantity))));
    }
}