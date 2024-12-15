package com.example.demo1.service.item;

import com.example.demo1.domain.item.Category;
import com.example.demo1.domain.item.Item;
import com.example.demo1.domain.user.User;
import com.example.demo1.dto.item.ItemDto;
import com.example.demo1.dto.item.ItemUpdateDto;
import com.example.demo1.dto.order.ItemSearch;
import com.example.demo1.dto.user.JoinDto;
import com.example.demo1.exception.Item.item.ItemAlreadyDeleteException;
import com.example.demo1.exception.Item.item.ItemOwnershipException;
import com.example.demo1.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired ItemService itemService;
    @Autowired UserService userService;
    @Autowired CategoryService categoryService;

    private final String ITEM_NAME = "상품1";
    private final String ITEM_EXPLANATION = "상품1의 설명입니다.";

    private Item item;
    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = createUser();
        category = createCategory();
        item = createItem(ITEM_NAME, category.getIdx(), user);
    }

    @Nested
    @DisplayName("상품 테스트")
    class itemTest {

        @Test
        @DisplayName("상품 저장 성공")
        void saveItemSuccess() {
            // given & when

            // then
            assertThat(item).isNotNull();
            assertThat(itemService.findAll(new ItemSearch()).size()).isEqualTo(1);
            assertThat(item.getItemName()).isEqualTo(ITEM_NAME);
            assertThat(item.getPrice()).isEqualTo(10000);
            assertThat(item.getExplanation()).isEqualTo(ITEM_EXPLANATION);
        }

        @Test
        @DisplayName("상품 저장 실패: 상품 가격이 음수인 경우")
        void saveItemFail() {
            // given
            ItemDto dto = new ItemDto("상품0", category.getIdx(), -1, "상품설명");

            // when & then
            assertThatThrownBy(() -> itemService.save(dto, null, user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상품 가격은 양수여야 합니다.");
        }

        @Test
        @DisplayName("상품 삭제 성공")
        void deleteItemSuccess() {
            // given

            // when
            itemService.delete(item.getIdx(), user);

            // then
            assertThat(item.getState()).isEqualTo(1);
        }

        @Test
        @DisplayName("상품 삭제 실패: 이미 삭제된 상품")
        void deleteItemFail() {
            // given
            itemService.delete(item.getIdx(), user);

            // when & then
            assertThatThrownBy(() -> itemService.delete(item.getIdx(), user))
                    .isInstanceOf(ItemAlreadyDeleteException.class)
                    .hasMessage("이미 삭제된 상품입니다.");
        }

        @Test
        @DisplayName("상품 수정 성공")
        void updateItemSuccess() {
            // given
            String updateItemName = "수정된 상품명";
            int updatePrice = 13000;
            String updateExplanation = "수정된 상품의 설명입니다.";

            // when
            System.out.println("category.getIdx() = " + category.getIdx());
            ItemUpdateDto updateDto = new ItemUpdateDto(item.getIdx(), updateItemName, category.getIdx(), updatePrice, updateExplanation);
            Item updateItem = itemService.update(updateDto, user);

            // then
            assertThat(updateItem.getItemName()).isEqualTo(updateItemName);
            assertThat(updateItem.getPrice()).isEqualTo(updatePrice);
            assertThat(updateItem.getExplanation()).isEqualTo(updateExplanation);
        }

        @Test
        @DisplayName("상품 수정 실패: 상품을 등록한 사용자가 아님")
        void updateItemFail() {
            // given
            User newUser = createNewUser();

            // when
            ItemUpdateDto updateDto = new ItemUpdateDto(item.getIdx(), "수정된 상품명", category.getIdx(), 13000, "수정된 상품의 설명입니다.");

            // then
            assertThatThrownBy(() -> itemService.update(updateDto, newUser))
                    .isInstanceOf(ItemOwnershipException.class);
        }
    }

    // 사용자 회원가입
    User createUser() {
        return getUser("testUser1", "testId1", "testPassword1", "010-0000-0001");
    }

    // 두 번째 사용자 회원가입
    User createNewUser() {
        return getUser("testUser2", "testId2", "testPassword2", "010-0000-0002");
    }

    private User getUser(String username, String loginId, String password, String tel) {
        JoinDto joinDto = JoinDto.builder()
                .username(username)
                .loginId(loginId)
                .password(password)
                .birth(LocalDate.now())
                .tel(tel)
                .address("서울시")
                .detail("1층")
                .build();
        return userService.join(joinDto);
    }

    // 카테고리 등록
    Category createCategory() {
        return categoryService.save("카테고리1");
    }

    // 상품 등록
    Item createItem(String itemName, Long categoryIdx, User user) {
        ItemDto itemDto = new ItemDto(itemName, categoryIdx, 10000, ITEM_EXPLANATION);
        Map<String, Object> save = itemService.save(itemDto, null, user);
        return (Item) save.get("item");
    }

}