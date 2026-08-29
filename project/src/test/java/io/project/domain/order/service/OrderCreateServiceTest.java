package io.project.domain.order.service;

import io.project.domain.delivery.entity.Delivery;
import io.project.domain.delivery.repository.DeliveryRepository;
import io.project.domain.order.dto.OrderCreateRequest;
import io.project.domain.order.dto.OrderItemRequest;
import io.project.domain.order.entity.Order;
import io.project.domain.order.entity.OrderItem;
import io.project.domain.order.entity.OrderStatus;
import io.project.domain.order.repository.OrderRepository;
import io.project.domain.product.entity.Product;
import io.project.domain.product.repository.ProductRepository;
import io.project.global.config.TestClock;
import io.project.global.config.TestTimeConfig;
import io.project.global.exception.NotFoundException;
import io.project.global.exception.OutOfStockException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Import(TestTimeConfig.class)
@Transactional
class OrderCreateServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestClock testClock;

    @AfterEach
    void cleanUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적인 주문 요청이면 주문이 생성된다")
    void createOrder() {

        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        productRepository.save(product);

        OrderItemRequest itemRequest =
                new OrderItemRequest(
                        product.getId(),
                        2
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest)
                );


        // when
        Order order =
                orderService.createOrder(request);

        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(order).isNotNull();
        assertThat(order.getId()).isNotNull();

        Order savedOrder =
                orderRepository.findById(order.getId())
                        .orElseThrow();

        assertThat(savedOrder.getId())
                .isEqualTo(order.getId());
    }

    @Test
    @DisplayName("주문 생성 시 주문 상태는 ORDERED이다")
    void createOrderStatus() {
        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        productRepository.save(product);

        OrderItemRequest itemRequest =
                new OrderItemRequest(
                        product.getId(),
                        2
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest)
                );

        // when
        Order order =
                orderService.createOrder(request);

        // then
        assertThat(order.getStatus())
                .isEqualTo(OrderStatus.ORDERED);

    }

    @Test
    @DisplayName("주문 상품 수만큼 OrderItem이 생성된다")
    void createOrderItems() {
        // given
        Product product1 = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        Product product2 = new Product(
                "TEST-ETHIOPIA-2",
                4300,
                10,
                "ethiopia2.jpg"
        );

        productRepository.save(product1);
        productRepository.save(product2);

        OrderItemRequest itemRequest1 =
                new OrderItemRequest(
                        product1.getId(),
                        2
                );

        OrderItemRequest itemRequest2 =
                new OrderItemRequest(
                        product2.getId(),
                        2
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest1, itemRequest2)
                );

        // when
        Order order =
                orderService.createOrder(request);

        // then
        assertThat(order.getOrderItems())
                .hasSize(request.items().size());

    }

    @Test
    @DisplayName("각 주문 상품의 수량이 OrderItem에 올바르게 저장된다")
    void saveOrderItemQuantity() {

        // given
        Product product1 = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        Product product2 = new Product(
                "TEST-ETHIOPIA-2",
                4300,
                10,
                "ethiopia2.jpg"
        );

        productRepository.save(product1);
        productRepository.save(product2);

        OrderItemRequest itemRequest1 =
                new OrderItemRequest(
                        product1.getId(),
                        3
                );

        OrderItemRequest itemRequest2 =
                new OrderItemRequest(
                        product2.getId(),
                        5
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest1, itemRequest2)
                );

        // when
        Order order = orderService.createOrder(request);

        // then
        OrderItem savedItem1 = order.getOrderItems()
                .stream()
                .filter(orderItem ->
                        orderItem.getProduct().getId() == (product1.getId())
                )
                .findFirst()
                .orElseThrow();

        OrderItem savedItem2 = order.getOrderItems()
                .stream()
                .filter(orderItem ->
                        orderItem.getProduct().getId() == (product2.getId())
                )
                .findFirst()
                .orElseThrow();

        assertThat(savedItem1.getQuantity())
                .isEqualTo(itemRequest1.quantity());

        assertThat(savedItem2.getQuantity())
                .isEqualTo(itemRequest2.quantity());
    }

    @Test
    @DisplayName("각 주문 상품의 가격이 OrderItem에 올바르게 저장된다")
    void saveOrderItemPrice() {

        // given
        Product product1 = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        Product product2 = new Product(
                "TEST-ETHIOPIA-2",
                4300,
                10,
                "ethiopia2.jpg"
        );

        productRepository.save(product1);
        productRepository.save(product2);

        OrderItemRequest itemRequest1 =
                new OrderItemRequest(
                        product1.getId(),
                        3
                );

        OrderItemRequest itemRequest2 =
                new OrderItemRequest(
                        product2.getId(),
                        5
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest1, itemRequest2)
                );

        // when
        Order order = orderService.createOrder(request);

        // then
        OrderItem savedItem1 = order.getOrderItems()
                .stream()
                .filter(orderItem ->
                        orderItem.getProduct().getId() == product1.getId()
                )
                .findFirst()
                .orElseThrow();

        OrderItem savedItem2 = order.getOrderItems()
                .stream()
                .filter(orderItem ->
                        orderItem.getProduct().getId() == product2.getId()
                )
                .findFirst()
                .orElseThrow();

        assertThat(savedItem1.getUnitPrice())
                .isEqualTo(product1.getPrice());

        assertThat(savedItem2.getUnitPrice())
                .isEqualTo(product2.getPrice());
    }

    @Test
    @DisplayName("상품 가격이 변경되어도 기존 OrderItem의 주문 가격은 유지된다")
    void keepOrderItemPriceAfterProductPriceChange() {

        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        productRepository.save(product);

        OrderItemRequest itemRequest1 =
                new OrderItemRequest(
                        product.getId(),
                        3
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest1)
                );

        Order order = orderService.createOrder(request);

        // when
        // - 주문 생성 후 Product 가격을 5300원으로 변경한다.
        product.update(null, 5300, null, null);

        // then
        OrderItem savedItem1 = order.getOrderItems()
                .stream()
                .filter(orderItem ->
                        orderItem.getProduct().getId() == product.getId()
                )
                .findFirst()
                .orElseThrow();


        assertThat(savedItem1.getUnitPrice())
                .isEqualTo(4800);

        assertThat(product.getPrice())
                .isEqualTo(5300);

    }

    @Test
    @DisplayName("주문 생성 시 상품 재고가 주문 수량만큼 감소한다")
    void decreaseProductStockWhenOrderCreated() {

        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        productRepository.save(product);

        OrderItemRequest itemRequest =
                new OrderItemRequest(
                        product.getId(),
                        3
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest)
                );

        // when
        orderService.createOrder(request);

        entityManager.flush();
        entityManager.clear();

        // then
        Product savedProduct = productRepository.findById(product.getId())
                .orElseThrow();

        assertThat(savedProduct.getStock()).isEqualTo(7);

    }

    @Test
    @DisplayName("재고보다 많은 수량을 주문하면 주문에 실패한다")
    void failOrderWhenStockIsInsufficient() {

        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                2,
                "ethiopia.jpg"
        );
        productRepository.save(product);

        OrderItemRequest itemRequest = new OrderItemRequest(
                product.getId(),
                3
        );
        OrderCreateRequest request = new OrderCreateRequest(
                "test@test.com",
                "서울시 강남구",
                "12345",
                List.of(itemRequest)
        );

        // when & then
        assertThatThrownBy(() ->
                orderService.createOrder(request)
        )
                .isInstanceOf(OutOfStockException.class);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("주문 생성 중 실패하면 이미 감소한 상품 재고도 롤백된다")
    void rollbackProductStockWhenOrderFails() {
        // given
        Product product1 = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        Product product2 = new Product(
                "TEST-ETHIOPIA-2",
                4300,
                3,
                "ethiopia2.jpg"
        );

        productRepository.save(product1);
        productRepository.save(product2);

        OrderItemRequest itemRequest1 =
                new OrderItemRequest(
                        product1.getId(),
                        3
                );

        OrderItemRequest itemRequest2 =
                new OrderItemRequest(
                        product2.getId(),
                        5
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest1, itemRequest2)
                );

        // when
        assertThatThrownBy(() ->
                orderService.createOrder(request)
        )
                .isInstanceOf(OutOfStockException.class);

        // then
        Product savedProduct1 = productRepository.findById(product1.getId())
                .orElseThrow();

        Product savedProduct2 = productRepository.findById(product2.getId())
                .orElseThrow();

        assertThat(savedProduct1.getStock()).isEqualTo(10);

        assertThat(savedProduct2.getStock()).isEqualTo(3);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 주문하면 주문에 실패한다")
    void failOrderWhenProductDoesNotExist() {
        // given
        OrderItemRequest itemRequest = new OrderItemRequest(
                999999,
                3
        );
        OrderCreateRequest request = new OrderCreateRequest(
                "test@test.com",
                "서울시 강남구",
                "12345",
                List.of(itemRequest)
        );

        // when & then
        assertThatThrownBy(() ->
                orderService.createOrder(request)
        )
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("새로운 배송 조건으로 주문하면 Delivery가 생성된다")
    void createDeliveryWhenNoMatchingDeliveryExists() {
        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );
        productRepository.save(product);

        OrderItemRequest itemRequest = new OrderItemRequest(
                product.getId(),
                3
        );
        OrderCreateRequest request = new OrderCreateRequest(
                "test@test.com",
                "서울시 강남구",
                "12345",
                List.of(itemRequest)
        );

        // when
        Order order = orderService.createOrder(request);

        int deliveryId = order.getDelivery().getId();

        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(order.getDelivery()).isNotNull();

        Delivery savedDelivery = deliveryRepository.findById(deliveryId)
                .orElseThrow();

        assertThat(savedDelivery.getId())
                .isEqualTo(deliveryId);

    }

    @Test
    @DisplayName("같은 배송 조건으로 주문하면 두 Order가 같은 Delivery에 묶인다")
    void groupOrdersIntoSameDelivery() {
        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );
        productRepository.save(product);

        OrderItemRequest itemRequest = new OrderItemRequest(
                product.getId(),
                2
        );
        OrderCreateRequest request = new OrderCreateRequest(
                "test@test.com",
                "서울시 강남구",
                "12345",
                List.of(itemRequest)
        );

        Order firstOrder = orderService.createOrder(request);

        // when
        Order secondOrder = orderService.createOrder(request);

        // then
        assertThat(secondOrder.getDelivery().getId())
                .isEqualTo(firstOrder.getDelivery().getId());

        assertThat(secondOrder.getId())
                .isNotEqualTo(firstOrder.getId());

    }

    @Test
    @DisplayName("배송 주소가 다르면 서로 다른 Delivery가 생성된다")
    void createDifferentDeliveryWhenAddressDiffers() {
        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );
        productRepository.save(product);

        OrderItemRequest itemRequest = new OrderItemRequest(
                product.getId(),
                2
        );
        OrderCreateRequest firstRequest = new OrderCreateRequest(
                "test@test.com",
                "서울시 강남구",
                "12345",
                List.of(itemRequest)
        );

        OrderCreateRequest secondRequest = new OrderCreateRequest(
                "test@test.com",
                "서울시 서초구",
                "12345",
                List.of(itemRequest)
        );

        Order firstOrder = orderService.createOrder(firstRequest);

        // when
        Order secondOrder = orderService.createOrder(secondRequest);

        // then
        assertThat(secondOrder.getDelivery().getId())
                .isNotEqualTo(firstOrder.getDelivery().getId());

    }

    @Test
    @DisplayName("테스트에서 고정한 시간이 주문 생성 시간으로 사용된다")
    void useFixedClockForOrderCreation() {

        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        productRepository.save(product);

        OrderItemRequest itemRequest =
                new OrderItemRequest(
                        product.getId(),
                        2
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest)
                );

        testClock.setInstant(
                LocalDateTime.of(2026, 8, 29, 13, 59, 59)
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toInstant()
        );

        // when
        Order order = orderService.createOrder(request);

        // then
        assertThat(order.getOrderedAt())
                .isEqualTo(
                        LocalDateTime.of(
                                2026, 8, 29,
                                13, 59, 59
                        )
                );
    }

    @Test
    @DisplayName("14시 이전 주문은 당일 processingDate로 처리된다")
    void setProcessingDateToTodayBeforeCutoff() {

        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        productRepository.save(product);

        OrderItemRequest itemRequest =
                new OrderItemRequest(
                        product.getId(),
                        2
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest)
                );

        testClock.setInstant(
                LocalDateTime.of(2026, 8, 29, 13, 59, 59)
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toInstant()
        );

        // when
        Order order = orderService.createOrder(request);

        // then
        assertThat(order.getDelivery().getProcessingDate())
                .isEqualTo(LocalDate.of(2026, 8, 29));
    }

    @Test
    @DisplayName("테스트에서 변경한 시간이 주문 생성 시간으로 사용된다")
    void useChangedTestClockForOrderCreation() {
        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        productRepository.save(product);

        OrderItemRequest itemRequest =
                new OrderItemRequest(
                        product.getId(),
                        2
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest)
                );

        testClock.setInstant(
                LocalDateTime.of(2026, 8, 29, 14, 0, 0)
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toInstant()
        );

        // when
        Order order = orderService.createOrder(request);

        // then
        assertThat(order.getOrderedAt())
                .isEqualTo(
                        LocalDateTime.of(
                                2026, 8, 29,
                                14, 0, 0
                        )
                );

    }

    @Test
    @DisplayName("14시 정각 주문은 다음날 processingDate로 처리된다")
    void setProcessingDateToNextDayAtCutoff() {

        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        productRepository.save(product);

        OrderItemRequest itemRequest =
                new OrderItemRequest(
                        product.getId(),
                        2
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest)
                );

        testClock.setInstant(
                LocalDateTime.of(2026, 8, 29, 14, 0, 0)
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toInstant()
        );

        // when
        Order order = orderService.createOrder(request);

        // then
        assertThat(order.getDelivery().getProcessingDate())
                .isEqualTo(LocalDate.of(2026, 8, 30));
    }

    @Test
    @DisplayName("13시 59분 59초와 14시 정각 주문은 서로 다른 Delivery에 묶인다")
    void separateDeliveryAtCutoffBoundary() {

        // given
        Product product = new Product(
                "TEST-ETHIOPIA-1",
                4800,
                10,
                "ethiopia.jpg"
        );

        productRepository.save(product);

        OrderItemRequest itemRequest =
                new OrderItemRequest(
                        product.getId(),
                        2
                );

        OrderCreateRequest request =
                new OrderCreateRequest(
                        "test@test.com",
                        "서울시 강남구",
                        "12345",
                        List.of(itemRequest)
                );

        // 1. 13:59:59로 시간 설정
        testClock.setInstant(
                LocalDateTime.of(2026, 8, 29, 13, 59, 59)
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toInstant()
        );

        // 2. 첫 번째 주문 생성
        Order firstOrder = orderService.createOrder(request);

        // when
        // 3. 시간을 14:00:00으로 변경
        testClock.setInstant(
                LocalDateTime.of(2026, 8, 29, 14, 0, 0)
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toInstant()
        );

        // 4. 동일한 배송 조건으로 두 번째 주문 생성
        Order secondOrder = orderService.createOrder(request);

        // then
        // 5. processingDate가 서로 다른지 확인
        assertThat(firstOrder.getDelivery().getProcessingDate())
                .isEqualTo(LocalDate.of(2026, 8, 29));

        assertThat(secondOrder.getDelivery().getProcessingDate())
                .isEqualTo(LocalDate.of(2026, 8, 30));

        // 6. 실제 Delivery도 서로 다른지 확인
        assertThat(secondOrder.getDelivery().getId())
                .isNotEqualTo(firstOrder.getDelivery().getId());
    }

}