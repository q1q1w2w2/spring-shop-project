package com.example.demo1.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDto {

    @NotNull(message = "idx는 필수값입니다.")
    private Long idx;

    private String itemName;

    private Long category;

    private Integer price;

    private String explanation;
}
