package com.example.demo1.controller.item;

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
import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/api/category")
    public ResponseEntity getAllCategory() {
        List<Object> response = new ArrayList<>();
        List<Category> list = categoryService.findAll();
        for (Category category : list) {
            response.add(new CategoryResponseDto(category));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/category")
    public ResponseEntity addCategory(@Validated @RequestBody CategoryRequestDto dto) {
        Category category = categoryService.save(dto.getCategoryName());

        CategoryResponseDto responseDto = new CategoryResponseDto(category);
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "카테고리가 생성되었습니다.");
        response.put("category", responseDto);

        return ResponseEntity.ok(response);
    }
}
