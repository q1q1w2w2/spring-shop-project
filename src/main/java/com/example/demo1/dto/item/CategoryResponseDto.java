package com.example.demo1.dto.item;

import com.example.demo1.domain.item.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseDto {

    private Long categoryIdx;
    private String categoryName;

    public CategoryResponseDto(Category category) {
        this.categoryIdx = category.getIdx();
        this.categoryName = category.getCategoryName();
    }
}
