package io.project.domain.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderCreateRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "주소를 입력해주세요")
        String address,

        @NotBlank(message = "우편번호를 입력해주세요")
        String postalCode,

        @NotEmpty(message = "주문 상품을 하나 이상 선택해주세요.")
        @Valid
        List<OrderItemRequest> items
){}