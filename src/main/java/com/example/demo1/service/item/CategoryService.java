package com.example.demo1.service.item;

import com.example.demo1.dto.item.CategoryResponseDto;
import com.example.demo1.entity.item.Category;
import com.example.demo1.exception.Item.category.CategoryAlreadyExistException;
import com.example.demo1.repository.item.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDto save(String categoryName) {
        if (categoryRepository.existsByCategoryName(categoryName)) {
            throw new CategoryAlreadyExistException();
        }

        Category category = new Category(categoryName);
        Category saveCategory = categoryRepository.save(category);
        return new CategoryResponseDto(saveCategory);
    }

    public List<CategoryResponseDto> findAll() {
        List<CategoryResponseDto> categoryDtoList = new ArrayList<>();

        List<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            categoryDtoList.add(new CategoryResponseDto(category));
        }
        return categoryDtoList;
    }
}
