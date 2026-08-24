package io.project.domain.product;

import io.project.domain.product.dto.ProductAddRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
                                .mapToObj(i -> Product.of("name" + i, i, i, "filename" + i))
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

        Product testProduct = Product.of("name1", 1, 1, "1");
        when(productRepository.save(testProduct))
                .thenReturn(testProduct);

        boolean isSaved = productService.save(new ProductAddRequest("name1", 1, 1, "1"));

        assertTrue(isSaved);
    }

    @Test
    @DisplayName("상품 저장 - 상품명 중복으로 실패")
    void saveFailed() {
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductService productService = new ProductService(productRepository);

        Product testProduct = Product.of("name1", 1, 1, "1");
        when(productRepository.save(any(Product.class)))
                .thenReturn(testProduct)
                .thenThrow(DataIntegrityViolationException.class);

        ProductAddRequest requestDto = new ProductAddRequest("name1", 1, 1, "1");
        boolean isSaved = productService.save(requestDto);
        assertTrue(isSaved);
        isSaved = productService.save(requestDto);
        assertFalse(isSaved);

        // 나중에 ProductService에서 GlobalExceptionHandler로 설정하면 테스트 케이스 수정해야 함
//        assertThrows(DataIntegrityViolationException.class,
//                () -> productService.save(requestDto));
    }
}