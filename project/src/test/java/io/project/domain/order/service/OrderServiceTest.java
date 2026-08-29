package io.project.domain.order.service;

import io.project.domain.delivery.repository.DeliveryRepository;
import io.project.domain.order.dto.DashBoardResponse;
import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderItemRequest;
import io.project.domain.order.dto.OrderListResponse;
import io.project.domain.order.entity.Order;
import io.project.domain.order.entity.OrderItem;
import io.project.domain.order.entity.OrderStatus;
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
        productA = productService.save(
                new ProductAddRequest(
                        "productA",
                        100,
                        100,
                        null
                )
        );

        productB = productService.save(
                new ProductAddRequest(
                        "productB",
                        100,
                        200,
                        null
                )
        );

        productC = productService.save(
                new ProductAddRequest(
                        "productC",
                        100,
                        300,
                        null
                )
        );

        productD = productService.save(
                new ProductAddRequest(
                        "productD",
                        100,
                        10000,
                        null
                )
        );
    }

    @Test
    @DisplayName("대시보드")
    void getDashBoard() {

        createOrder(
                "test@test",
                productA.getId(),
                2
        ); // 수익 200

        createOrder(
                "test@test",
                productB.getId(),
                5
        ); // 수익 1000

        createOrder(
                "test@test",
                productC.getId(),
                10
        ); // 수익 3000

        createOrder(
                "test@test",
                productD.getId(),
                1
        ); // 수익 10000

        DashBoardResponse dashBoard =
                orderService.getDashBoard(
                        LocalDate.now(),
                        LocalDate.now()
                );

        System.out.println(
                "dashBoard = " + dashBoard
        );

        // 총 수익 검증
        assertEquals(
                14200,
                dashBoard.revenueDashBoards()
                        .stream()
                        .mapToInt(
                                d -> (int) d.totalPrice()
                        )
                        .sum()
        );

        // 팔린 개수 Top 3 검증
        assertArrayEquals(
                new String[]{
                        "productC",
                        "productB",
                        "productA"
                },
                dashBoard.soldTop3DashBoards()
                        .stream()
                        .map(d -> d.name())
                        .toArray(String[]::new)
        );

        // 수익 Top 3 검증
        assertArrayEquals(
                new String[]{
                        "productD",
                        "productC",
                        "productB"
                },
                dashBoard.revenueTop3DashBoards()
                        .stream()
                        .map(d -> d.name())
                        .toArray(String[]::new)
        );
    }

    @Test
    @DisplayName("주문 상세 조회")
    void findById() {

        Order savedOrder =
                createOrder(
                        "test@test",
                        productA.getId(),
                        2
                );

        Order order =
                orderService.findById(
                        savedOrder.getId()
                );

        assertEquals(
                savedOrder.getId(),
                order.getId()
        );

        assertEquals(
                "test@test",
                order.getDelivery()
                        .getEmail()
        );

        assertEquals(
                "서울시 강남구",
                order.getDelivery()
                        .getAddress()
        );

        assertEquals(
                "12345",
                order.getDelivery()
                        .getPostalCode()
        );

        assertEquals(
                1,
                order.getOrderItems()
                        .size()
        );

        OrderItem orderItem =
                order.getOrderItems()
                        .get(0);

        assertEquals(
                "productA",
                orderItem.getProduct()
                        .getName()
        );

        assertEquals(
                2,
                orderItem.getQuantity()
        );

        assertEquals(
                100,
                orderItem.getUnitPrice()
        );
    }

    @Test
    @DisplayName("주문 목록 조회")
    void orderList() {

        createOrder(
                "test@test",
                productB.getId(),
                2
        );

        createOrder(
                "test@test",
                productB.getId(),
                5
        );

        createOrder(
                "test@test",
                productC.getId(),
                10
        );

        createOrder(
                "other@test",
                productD.getId(),
                1
        );

        List<OrderListResponse> orderList =
                orderService.orderList(
                        "test@test",
                        null,
                        null
                );

        assertEquals(
                3,
                orderList.size()
        );
    }

    @Test
    @DisplayName("주문 목록 조회 - 시작일만 입력")
    void orderListStartDateOnly() {

        assertThrows(
                InvalidException.class,
                () ->
                        orderService.orderList(
                                "test@test",
                                LocalDate.now(),
                                null
                        )
        );
    }

    @Test
    @DisplayName("주문 목록 조회 - 종료일만 입력")
    void orderListEndDateOnly() {

        assertThrows(
                InvalidException.class,
                () ->
                        orderService.orderList(
                                "test@test",
                                null,
                                LocalDate.now()
                        )
        );
    }

    @Test
    @DisplayName("존재하지 않는 주문 상세 조회")
    void findByIdNotFound() {

        assertThrows(
                NotFoundException.class,
                () ->
                        orderService.findById(
                                999999
                        )
        );
    }

    @Test
    @DisplayName("주문 목록 조회 - 시작일이 종료일보다 이후인 경우")
    void orderListStartDateAfterEndDate() {

        assertThrows(
                InvalidException.class,
                () ->
                        orderService.orderList(
                                "test@test",
                                LocalDate.of(
                                        2026,
                                        8,
                                        30
                                ),
                                LocalDate.of(
                                        2026,
                                        8,
                                        29
                                )
                        )
        );
    }

    @Test
    @DisplayName("주문 주소와 우편번호 수정")
    void updateOrder_success() {

        // 주소와 우편번호만 수정되고 수량은 그대로 유지되는지 테스트
        Order order =
                createOrder(
                        "update@test.com",
                        productA.getId(),
                        2
                );

        int originalQuantity =
                order.getOrderItems()
                        .get(0)
                        .getQuantity();

        orderService.updateOrder(
                order.getId(),
                "서울시 송파구",
                "05678"
        );

        assertEquals(
                "서울시 송파구",
                order.getDelivery()
                        .getAddress()
        );

        assertEquals(
                "05678",
                order.getDelivery()
                        .getPostalCode()
        );

        assertEquals(
                originalQuantity,
                order.getOrderItems()
                        .get(0)
                        .getQuantity()
        );
    }

    @Test
    @DisplayName("취소된 주문 수정 불가")
    void updateOrder_canceledOrder_fail() {

        // 이미 취소된 주문은 주소와 우편번호를 수정할 수 없는지 테스트
        Order order =
                createOrder(
                        "cancel-update@test.com",
                        productA.getId(),
                        2
                );

        orderService.cancelOrder(
                order.getId()
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                orderService.updateOrder(
                                        order.getId(),
                                        "서울시 송파구",
                                        "05678"
                                )
                );

        assertEquals(
                "취소된 주문은 수정할 수 없습니다.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("주문 취소")
    void cancelOrder_success() {

        // 주문 취소 시 주문 상태가 CANCELED로 변경되는지 테스트
        Order order =
                createOrder(
                        "cancel@test.com",
                        productA.getId(),
                        2
                );

        assertEquals(
                OrderStatus.ORDERED,
                order.getStatus()
        );

        orderService.cancelOrder(
                order.getId()
        );

        assertEquals(
                OrderStatus.CANCELED,
                order.getStatus()
        );
    }

    @Test
    @DisplayName("주문 취소 시 재고 복구")
    void cancelOrder_restoreStock_success() {

        // 주문 취소 시 주문했던 수량만큼 재고가 복구되는지 테스트
        int quantity = 3;

        Order order =
                createOrder(
                        "stock@test.com",
                        productA.getId(),
                        quantity
                );

        int stockAfterOrder =
                order.getOrderItems()
                        .get(0)
                        .getProduct()
                        .getStock();

        orderService.cancelOrder(
                order.getId()
        );

        int stockAfterCancel =
                order.getOrderItems()
                        .get(0)
                        .getProduct()
                        .getStock();

        assertEquals(
                stockAfterOrder + quantity,
                stockAfterCancel
        );
    }

    @Test
    @DisplayName("중복 주문 취소 불가")
    void cancelOrder_duplicate_fail() {

        // 이미 취소된 주문은 다시 취소할 수 없는지 테스트
        Order order =
                createOrder(
                        "duplicate@test.com",
                        productA.getId(),
                        2
                );

        orderService.cancelOrder(
                order.getId()
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                orderService.cancelOrder(
                                        order.getId()
                                )
                );

        assertEquals(
                "이미 취소된 주문입니다.",
                exception.getMessage()
        );
    }

    private Order createOrder(
            String email,
            int productId,
            int quantity
    ) {

        return orderService.createOrder(
                new OrderCreateRequest(
                        email,
                        "서울시 강남구",
                        "12345",
                        List.of(
                                new OrderItemRequest(
                                        productId,
                                        quantity
                                )
                        )
                )
        );
    }
}