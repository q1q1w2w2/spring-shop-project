package com.example.demo1.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrdersDto {

    @NotNull(message = "itemIdx는 필수값입니다.")
    private Long itemIdx;

    @NotNull(message = "quantity는 필수값입니다.")
    @Positive
    private Integer quantity;

}

