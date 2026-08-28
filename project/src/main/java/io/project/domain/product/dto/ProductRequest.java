package io.project.domain.product.dto;

import io.project.global.exception.InvalidException;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

public class ProductRequest {
    public record ProductUpdateRequest(
            String name,

            @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
            Integer stock,

            @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
            Integer price,

            String fileName,

            MultipartFile image
    ) {
        public ProductUpdateRequest {
            if (name != null && name.isBlank()) {
                throw new InvalidException("이름은 공백일 수 없습니다.");
            }
        }
    }

    public record ProductAddRequest(
            @NotNull(message = "상품명을 입력해주세요.")
            String name,
            @NotNull(message = "상품의 재고를 입력해주세요.")
            Integer stock,
            @NotNull(message = "상품의 가격을 입력해주세요.")
            Integer price) {
    }
}