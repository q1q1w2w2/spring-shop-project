package com.example.demo1.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemDto {

    @NotBlank(message = "itemName은 필수값입니다.")
    private String itemName;

    @NotNull(message = "category는 필수값입니다.")
    private Long category;

    @NotNull(message = "price는 필수값입니다.")
    @Positive // 양수만 허용
    private int price;

    private String explanation;

}
