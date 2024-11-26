package com.example.demo1.dto.item;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequestDto {

    @NotNull(message = "itemIdx는 필수값입니다.")
    private Long itemIdx;
}
