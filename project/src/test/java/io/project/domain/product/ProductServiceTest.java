package io.project.domain.product;


import io.project.domain.product.entity.Product;
import io.project.domain.product.repository.ProductRepository;
import io.project.domain.product.service.ProductService;
import io.project.global.exception.DuplicatedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.project.domain.product.dto.ProductRequest.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
class ProductServiceTest {

    private final String IMAGE_PATH = "./test-images";

    // 테스트 중 저장 된 이미지 제거
    @AfterEach
    void cleanUp() throws IOException {
        Path dir = Path.of(IMAGE_PATH).toAbsolutePath().normalize();

        if (Files.notExists(dir)) {
            return;
        }

        try (var paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new UncheckedIOException("파일 제거 실패", e);
                        }
                    });
        }
    }

    @Test
    @DisplayName("상품 목록 출력")
    void findAll() {
        // 목 레파지토리 생성 및 서비스에 주입
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductService productService = new ProductService(productRepository, null);

        // 출력할 데이터 설정
        when(productRepository.findAllByDeletedAtIsNull())
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
    @DisplayName("상품 저장(이미지 제외)")
    void saveNoImage() {
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductService productService = new ProductService(productRepository, null);

        Product testProduct = new Product("name1", 1, 1, "1");
        when(productRepository.save(testProduct))
                .thenReturn(testProduct);

        Product product = productService.save(new ProductAddRequest("name1", 1, 1, null));

        assertEquals(testProduct.getName(), product.getName());
        assertEquals(testProduct.getPrice(), product.getPrice());
        assertEquals(testProduct.getStock(), product.getStock());
    }

    @Test
    @DisplayName("상품 저장(이미지 제외) - 상품명 중복으로 실패")
    void saveFailedNoImage() {
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductService productService = new ProductService(productRepository, null);

        Product testProduct = new Product("name1", 1, 1, "1");
        when(productRepository.save(any(Product.class)))
                .thenReturn(testProduct)
                .thenThrow(DataIntegrityViolationException.class);

        ProductAddRequest requestDto = new ProductAddRequest("name1", 1, 1, null);

        // 첫번째 실행은 예외가 발생되지 않음
        productService.save(requestDto);

        // 두번째 실행은 unique 규칙이 설정된 name을 같은 이름으로 등록했으므로 예외 발생
        assertThrows(DuplicatedException.class,
                () -> productService.save(requestDto));
    }

    @Test
    @DisplayName("상품 저장(이미지 포함)")
    void saveWithImage() {
        ProductRepository productRepository = mock(ProductRepository.class);
        ProductService productService = new ProductService(productRepository, IMAGE_PATH);

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "coffee.png",
                "image/png",
                "dummy".getBytes()
        );

        ProductAddRequest requestDto =
                new ProductAddRequest("name", 1, 1, image);

        Product product = productService.save(requestDto);

        assertEquals("name", product.getName());
        assertEquals(1, product.getPrice());
        assertEquals(1, product.getStock());

        // UUID 기반 파일명이 생성됐는지 확인
        assertNotNull(product.getFileName());
        assertTrue(product.getFileName().endsWith(".png"));

        String uuidPart =
                product.getFileName()
                        .substring(0, product.getFileName().length() - 4);

        assertDoesNotThrow(() -> UUID.fromString(uuidPart));

        // 실제 테스트 이미지 디렉터리에 파일이 생성됐는지 확인
        Path savedImage =
                Path.of(IMAGE_PATH)
                        .toAbsolutePath()
                        .normalize()
                        .resolve(product.getFileName());

        assertTrue(Files.exists(savedImage));
    }
}