package com.example.demo1.controller.item;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.entity.item.Category;
import com.example.demo1.dto.item.CategoryRequestDto;
import com.example.demo1.dto.item.CategoryResponseDto;
import com.example.demo1.service.item.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/api/category")
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getAllCategory() {
        List<CategoryResponseDto> categoryDtoList = new ArrayList<>();
        List<Category> list = categoryService.findAll();
        for (Category category : list) {
            categoryDtoList.add(new CategoryResponseDto(category));
        }

        ApiResponse<List<CategoryResponseDto>> response = ApiResponse.success(OK, categoryDtoList);
        return ResponseEntity.status(OK).body(response);
    }

    @PostMapping("/api/category")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> addCategory(@Validated @RequestBody CategoryRequestDto dto) {
        Category category = categoryService.save(dto.getCategoryName());
        CategoryResponseDto responseDto = new CategoryResponseDto(category);

        ApiResponse<CategoryResponseDto> response = ApiResponse.success(CREATED, "카테고리가 생성되었습니다.", responseDto);
        return ResponseEntity.ok(response);
    }
}
