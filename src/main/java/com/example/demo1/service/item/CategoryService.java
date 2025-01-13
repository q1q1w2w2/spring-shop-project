package com.example.demo1.service.item;

import com.example.demo1.entity.item.Category;
import com.example.demo1.exception.Item.category.CategoryAlreadyExistException;
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
        if (categoryRepository.existsByCategoryName(categoryName)) {
            throw new CategoryAlreadyExistException();
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
