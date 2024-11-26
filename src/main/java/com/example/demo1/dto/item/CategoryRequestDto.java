package com.example.demo1.dto.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDto {

    @NotBlank(message = "카테고리 이름은 비어있을 수 없습니다.")
    private String categoryName;
}
