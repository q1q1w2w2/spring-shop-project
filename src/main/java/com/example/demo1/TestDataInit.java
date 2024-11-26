package com.example.demo1;

import com.example.demo1.domain.item.Category;
import com.example.demo1.dto.user.JoinDto;
import com.example.demo1.repository.item.CategoryRepository;
import com.example.demo1.service.item.ItemService;
import com.example.demo1.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

//@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final ItemService itemService;

    @PostConstruct
    public void init() {
        categoryRepository.save(new Category("음식", LocalDateTime.now(), LocalDateTime.now()));
        categoryRepository.save(new Category("과일", LocalDateTime.now(), LocalDateTime.now()));
        categoryRepository.save(new Category("장난감", LocalDateTime.now(), LocalDateTime.now()));
        categoryRepository.save(new Category("주류", LocalDateTime.now(), LocalDateTime.now()));

        userService.joinAdmin(JoinDto.builder()
                .username("admin")
                .loginId("admin")
                .password("admin")
                .birth(LocalDate.of(2000, 1, 1))
                .address("서울")
                .detail("구로")
                .tel("010-1231-1313")
                .build()
        );

        userService.join(JoinDto.builder()
                .username("user1")
                .loginId("test1")
                .password("test1")
                .birth(LocalDate.of(1999, 12, 3))
                .address("경기")
                .detail("파주")
                .tel("010-4561-3393")
                .build()
        );
    }
}
