package com.example.demo1.service.item;

import com.example.demo1.domain.item.Category;
import com.example.demo1.exception.Item.category.CategoryAlreadyExist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired CategoryService categoryService;

    @Test
    @DisplayName("카테고리 추가 성공")
    void addCategory() {
        // given
        categoryService.save("카테고리1");

        // when
        boolean isCategorySaved = categoryService.findAll()
                .stream()
                .anyMatch(c -> c.getCategoryName().equals("카테고리1"));

        // then
        assertThat(isCategorySaved).isTrue();
    }


    @Test
    @DisplayName("카테고리 추가 실패: 중복된 카테고리")
    void failAddCategory() {
        // given
        categoryService.save("카테고리1");

        // when & then
        assertThatThrownBy(() -> categoryService.save("카테고리1"))
                .isInstanceOf(CategoryAlreadyExist.class);
    }

    @Test
    @DisplayName("모든 카테고리 조회 성공")
    void findAllCategory() {
        // given
        categoryService.save("카테고리1");
        categoryService.save("카테고리2");
        categoryService.save("카테고리3");

        // when & then
        assertThat(categoryService.findAll().size()).isEqualTo(3);

    }

}