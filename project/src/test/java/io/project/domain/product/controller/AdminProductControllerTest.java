package io.project.domain.product.controller;

import io.project.domain.product.service.ProductService;
import io.project.global.exception.BusinessException;
import io.project.global.exception.InvalidException;
import io.project.global.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static io.project.domain.product.dto.ProductRequest.ProductUpdateRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Nested
    @DisplayName("상품 정보 수정 기능")
    class UpdateProduct {

        @Test
        @DisplayName("성공: 유효한 데이터를 주입하면 상품 수정 API가 200 OK를 반환한다")
        void success_updateProduct() throws Exception {
            Integer productId = 1;
            MockMultipartFile imageFile = new MockMultipartFile(
                    "image", "test.jpg", "image/jpeg", "content".getBytes()
            );

            doNothing().when(productService).updateProduct(eq(productId), any(), eq(imageFile));

            mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/products/" + productId)
                            .file(imageFile)
                            .param("name", "상품명")
                            .param("stock", "50")
                            .param("price", "15000")
                            .with(request -> { request.setMethod("PATCH"); return request; }))
                    .andExpect(status().isOk())
                    .andExpect(content().string("상품 정보가 성공적으로 수정되었습니다."));

            verify(productService, times(1)).updateProduct(eq(productId), any(), eq(imageFile));
        }

        @Test
        @DisplayName("성공: 일부 수정 데이터를 null로 보내도 API 요청은 정상 처리된다 (부분 수정 검증)")
        void success_partialUpdateWithNull() throws Exception {
            Integer productId = 1;

            doNothing().when(productService).updateProduct(eq(productId), any(), any());

            mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/products/" + productId)
                            .param("price", "20000")
                            .param("imagePath", "new-image.jpg")
                            .with(request -> { request.setMethod("PATCH"); return request; }))
                    .andExpect(status().isOk());

            verify(productService, times(1)).updateProduct(eq(productId), any(), isNull());
        }

        @Test
        @DisplayName("성공: 새로운 첨부 이미지가 없어도(null) 상품 수정이 정상 작동한다")
        void success_noImageAttached() throws Exception {
            Integer productId = 1;

            doNothing().when(productService).updateProduct(eq(productId), any(), any());

            mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/products/" + productId)
                            .param("name", "상품명 변경")
                            .param("stock", "10")
                            .param("price", "5000")
                            .with(request -> { request.setMethod("PATCH"); return request; }))
                    .andExpect(status().isOk());

            verify(productService, times(1)).updateProduct(eq(productId), any(), isNull());
        }

        @Test
        @DisplayName("성공: 상품 가격을 경계값인 0원으로 수정해도 정상 처리된다")
        void success_priceZeroBound() throws Exception {
            Integer productId = 1;

            doNothing().when(productService).updateProduct(eq(productId), any(), any());

            mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/products/" + productId)
                            .param("name", "무료상품")
                            .param("stock", "5")
                            .param("price", "0")
                            .with(request -> { request.setMethod("PATCH"); return request; }))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("실패: 이름에 빈 공백이 들어오면 InvalidException 예외가 발생한다")
        void fail_blankName() {
            Integer productId = 1;
            String blankName = "   ";
            MockMultipartFile imageFile = new MockMultipartFile(
                    "image", "test.jpg", "image/jpeg", "content".getBytes()
            );

            assertThatThrownBy(() -> new ProductUpdateRequest(blankName, 50, 15000, "test.jpg", imageFile))
                    .isInstanceOf(InvalidException.class)
                    .hasMessageContaining("이름은 공백일 수 없습니다.");

            verify(productService, never()).updateProduct(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("상품 비활성화(삭제) 기능")
    class DeleteProduct {

        @Test
        @DisplayName("성공: 존재하는 상품 ID를 넘기면 정상적으로 삭제 처리되고 200 OK 메시지를 반환한다")
        void success_deleteProduct() throws Exception {
            Integer productId = 1;

            doNothing().when(productService).deleteProduct(productId);

            mockMvc.perform(MockMvcRequestBuilders.delete("/admin/products/" + productId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("상품이 성공적으로 삭제되었습니다."));

            verify(productService, times(1)).deleteProduct(productId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 상품 ID를 넘기면 서비스에서 NotFoundException 예외가 발생한다")
        void fail_productNotFound() throws Exception {
            Integer nonExistProductId = 999;

            doThrow(new NotFoundException("존재하지 않는 상품입니다."))
                    .when(productService).deleteProduct(nonExistProductId);

            mockMvc.perform(MockMvcRequestBuilders.delete("/admin/products/" + nonExistProductId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("실패: 이미 비활성화(판매 중지)된 상품을 다시 비활성화하려고 하면 BusinessException 예외가 발생한다")
        void fail_alreadyDeletedProduct() throws Exception {
            Integer alreadyDeletedProductId = 2;

            doThrow(new BusinessException("이미 판매 중지된 상품입니다."))
                    .when(productService).deleteProduct(alreadyDeletedProductId);

            mockMvc.perform(MockMvcRequestBuilders.delete("/admin/products/" + alreadyDeletedProductId))
                    .andExpect(status().isBadRequest());
        }
    }
}
