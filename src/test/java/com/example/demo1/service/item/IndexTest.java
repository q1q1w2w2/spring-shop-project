package com.example.demo1.service.item;

import com.example.demo1.dto.item.CategoryResponseDto;
import com.example.demo1.dto.item.ItemDto;
import com.example.demo1.dto.order.ItemSearch;
import com.example.demo1.dto.user.JoinDto;
import com.example.demo1.entity.item.Category;
import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.user.User;
import com.example.demo1.repository.item.CategoryRepository;
import com.example.demo1.repository.item.ItemRepository;
import com.example.demo1.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class IndexTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void initData() {
        JoinDto joinDto = new JoinDto("사용자1", "loginId", "password", LocalDate.now(), "010-1232-4112", "주소", "비밀");
        User user = userService.join(joinDto);
        CategoryResponseDto category = categoryService.save("카테고리1");

        for (int i = 0; i < 100000; i++) {
            ItemDto itemDto = new ItemDto("상품" + i, category.getCategoryIdx(), 10000, "설명");
            itemService.save(itemDto, null, user);
        }
    }

    @Test
    void test() {
        JoinDto joinDto = new JoinDto("사용자1", "loginId", "password", LocalDate.now(), "010-1232-4112", "주소", "비밀");
        User user = userService.join(joinDto);
        CategoryResponseDto category = categoryService.save("카테고리1");

        for (int i = 0; i < 50000; i++) {
            ItemDto itemDto = new ItemDto("상품" + i, category.getCategoryIdx(), 10000, "설명");
            itemService.save(itemDto, null, user);
        }

        ItemSearch itemSearch = new ItemSearch();
        itemSearch.setItemName("상품21345");
        itemSearch.setCategoryIdx(category.getCategoryIdx());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Item> all = itemService.findAll(itemSearch);
        for (Item item : all) {
            System.out.println("item.getItemName() = " + item.getItemName());
        }
        stopWatch.stop();
        System.out.println("소요 시간: " + stopWatch.getTotalTimeMillis() + "ms");
    }
}
