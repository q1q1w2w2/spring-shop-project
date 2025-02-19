package com.example.demo1.controller.item;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.dto.item.CategoryRequestDto;
import com.example.demo1.dto.item.CategoryResponseDto;
import com.example.demo1.service.item.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static com.example.demo1.util.common.ApiResponseUtil.*;
import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/api/category")
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getAllCategory() {
        List<CategoryResponseDto> categories = categoryService.findAll();
        return createResponse(OK, categories);
    }

    @PostMapping("/api/category")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> addCategory(@Valid @RequestBody CategoryRequestDto dto) {
        CategoryResponseDto category = categoryService.save(dto.getCategoryName());
        return createResponse(CREATED, "카테고리가 생성되었습니다.", category);
    }
}
