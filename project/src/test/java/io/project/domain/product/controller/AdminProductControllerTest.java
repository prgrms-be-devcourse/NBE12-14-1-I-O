package io.project.domain.product.controller;

import io.project.domain.product.service.ProductService;
import io.project.global.exception.InvalidException; // 실제 InvalidException 패키지 경로 확인 필요
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static io.project.domain.product.dto.ProductRequest.ProductUpdateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    private AdminProductController adminProductController;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        adminProductController = new AdminProductController(productService);
    }

    @Nested
    @DisplayName("상품 정보 수정 기능")
    class UpdateProduct {

        @Test
        @DisplayName("성공: 유효한 데이터를 주입하면 상품 수정 API가 200 OK를 반환한다")
        void success_updateProduct() {
            Integer productId = 1;
            MockMultipartFile imageFile = new MockMultipartFile(
                    "image", "test.jpg", "image/jpeg", "content".getBytes()
            );

            ProductUpdateRequest request = new ProductUpdateRequest(
                    "상품명", 50, 15000, "test.jpg", imageFile
            );

            doNothing().when(productService).updateProduct(eq(productId), any(), eq(imageFile));

            ResponseEntity<String> response = adminProductController.updateProduct(productId, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo("상품 정보가 성공적으로 수정되었습니다.");

            verify(productService, times(1)).updateProduct(eq(productId), any(), eq(imageFile));
        }

        @Test
        @DisplayName("성공: 일부 수정 데이터를 null로 보내도 API 요청은 정상 처리된다 (부분 수정 검증)")
        void success_partialUpdateWithNull() {
            Integer productId = 1;
            ProductUpdateRequest request = new ProductUpdateRequest(
                    null, null, 20000, "new-image.jpg", null
            );

            doNothing().when(productService).updateProduct(eq(productId), any(), any());

            ResponseEntity<String> response = adminProductController.updateProduct(productId, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            verify(productService, times(1)).updateProduct(eq(productId), any(), isNull());
        }

        @Test
        @DisplayName("성공: 새로운 첨부 이미지가 없어도(null) 상품 수정이 정상 작동한다")
        void success_noImageAttached() {
            Integer productId = 1;
            ProductUpdateRequest request = new ProductUpdateRequest(
                    "상품명 변경", 10, 5000, null, null
            );

            doNothing().when(productService).updateProduct(eq(productId), any(), any());

            ResponseEntity<String> response = adminProductController.updateProduct(productId, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(productService, times(1)).updateProduct(eq(productId), any(), isNull());
        }

        @Test
        @DisplayName("성공: 상품 가격을 경계값인 0원으로 수정해도 정상 처리된다")
        void success_priceZeroBound() {
            Integer productId = 1;
            ProductUpdateRequest request = new ProductUpdateRequest(
                    "무료상품", 5, 0, null, null
            );

            doNothing().when(productService).updateProduct(eq(productId), any(), any());

            ResponseEntity<String> response = adminProductController.updateProduct(productId, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("실패: 이름에 빈 공백이 들어오면 InvalidException 예외가 발생한다")
        void fail_blankName() {
            Integer productId = 1;
            String blankName = "   "; // 공백 입력
            MockMultipartFile imageFile = new MockMultipartFile(
                    "image", "test.jpg", "image/jpeg", "content".getBytes()
            );

            assertThatThrownBy(() -> new ProductUpdateRequest(blankName, 50, 15000, "test.jpg", imageFile))
                    .isInstanceOf(InvalidException.class)
                    .hasMessageContaining("이름은 공백일 수 없습니다.");

            verify(productService, never()).updateProduct(any(), any(), any());
        }
    }
}
