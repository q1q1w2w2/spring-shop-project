package com.example.demo1.service.item;

import com.example.demo1.domain.item.Category;
import com.example.demo1.dto.item.CategoryResponseDto;
import com.example.demo1.exception.Item.category.CategoryAlreadyExist;
import com.example.demo1.repository.item.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category save(String categoryName) {
        // 카테고리명 중복검사
        if (categoryRepository.existsByCategoryName(categoryName)) {
            throw new CategoryAlreadyExist();
        }

        Category category = new Category(
                categoryName,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        return categoryRepository.save(category);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
