package io.project.domain.product;


import io.project.domain.product.entity.Product;
import io.project.domain.product.repository.ProductRepository;
import io.project.domain.product.service.ProductService;
import io.project.global.exception.DuplicatedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import io.project.domain.product.dto.ProductRequest.*;

@SpringBootTest
class ProductServiceTest {

    @Test
    @DisplayName("상품 목록 출력")
    void findAll() {
        // 목 레파지토리 생성 및 서비스에 주입
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductService productService = new ProductService(productRepository);

        // 출력할 데이터 설정
        when(productRepository.findAll())
                .thenReturn(
                        IntStream.rangeClosed(1, 10)
                                .mapToObj(i -> new Product("name" + i, i, i, "filename" + i))
                                .toList()
                );

        // 서비스 메서드 호출
        List<Product> result = productService.findAll();

        // 검증
        assertEquals(10, result.size());
        assertEquals("name1", result.get(0).getName());
    }

    @Test
    @DisplayName("상품 저장")
    void save() {
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductService productService = new ProductService(productRepository);

        Product testProduct = new Product("name1", 1, 1, "1");
        when(productRepository.save(testProduct))
                .thenReturn(testProduct);

        productService.save(new ProductAddRequest("name1", 1, 1), null);
        // 예외가 발생하지 않으면 성공
    }

    @Test
    @DisplayName("상품 저장 - 상품명 중복으로 실패")
    void saveFailed() {
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductService productService = new ProductService(productRepository);

        Product testProduct = new Product("name1", 1, 1, "1");
        when(productRepository.save(any(Product.class)))
                .thenReturn(testProduct)
                .thenThrow(DataIntegrityViolationException.class);

        ProductAddRequest requestDto = new ProductAddRequest("name1", 1, 1);

        // 첫번째 실행은 예외가 발생되지 않음
        productService.save(requestDto, null);

        // 두번째 실행은 unique 규칙이 설정된 name을 같은 이름으로 등록했으므로 예외 발생
        assertThrows(DuplicatedException.class,
                () -> productService.save(requestDto, null));
    }
}