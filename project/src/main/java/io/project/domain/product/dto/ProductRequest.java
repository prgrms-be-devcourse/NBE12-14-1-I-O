package io.project.domain.product.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public class ProductRequest {
    public record ProductUpdateRequest(
            @Pattern(regexp = ".*\\S.*")
            String name,

            @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
            Integer stock,

            @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
            Integer price,

            String fileName
    ) {
    }
}