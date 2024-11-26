package com.example.demo1.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDto {
    @NotNull(message = "orderIdx는 필수값입니다.")
    private Long orderIdx;
}
