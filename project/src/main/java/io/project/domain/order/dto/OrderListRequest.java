package io.project.domain.order.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record OrderListRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        String email,
        LocalDate startDate,
        LocalDate endDate
) {}
