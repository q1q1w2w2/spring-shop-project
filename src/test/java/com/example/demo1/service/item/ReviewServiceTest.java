package com.example.demo1.service.item;

import com.example.demo1.entity.item.*;
import com.example.demo1.entity.user.User;
import com.example.demo1.dto.item.ItemDto;
import com.example.demo1.dto.order.CreateOrdersDto;
import com.example.demo1.dto.order.CreateReviewDto;
import com.example.demo1.dto.order.OrderResult;
import com.example.demo1.dto.order.ReviewSearch;
import com.example.demo1.dto.user.JoinDto;
import com.example.demo1.exception.Item.review.ReviewNotAllowedException;
import com.example.demo1.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired ReviewService reviewService;
    @Autowired OrderService orderService;
    @Autowired UserService userService;
    @Autowired ItemService itemService;
    @Autowired CategoryService categoryService;
    @Autowired OrderLogService orderLogService;

    private User user;
    private Category category;
    private Item item;
    private OrderResult orderResult;

    private final int REVIEW_COMP = 1;

    @BeforeEach
    void setUp() {
        user = createUser();
        category = createCategory();
        item = createItem("상품1", category.getIdx(), user);

        List<CreateOrdersDto> list = new ArrayList<>();
        CreateOrdersDto ordersDto = new CreateOrdersDto(item.getIdx(), 100);
        list.add(ordersDto);
        orderResult = createOrder(list);

        // 주문 완료 상태로 변경
        Orders orders = orderResult.getOrders();
        orderService.completeOrders(orders.getIdx());
    }

    @Nested
    @DisplayName("리뷰 테스트")
    class ReviewTest {

        @Test
        @DisplayName("리뷰 작성 성공")
        void reviewSuccess() {
            // given
            List<OrderLog> orderLogs = orderResult.getOrderLogs();

            // when
            CreateReviewDto dto = new CreateReviewDto(orderLogs.get(0).getIdx(), "리뷰 내용입니다.", 5);
            Review review = reviewService.saveReview(dto, user.getId());
            List<Review> findAllReview = reviewService.getReview(new ReviewSearch());

            // then
            assertThat(findAllReview.size()).isEqualTo(1);
            assertThat(orderLogs.get(0).getReview()).isEqualTo(REVIEW_COMP);
        }

        @Test
        @DisplayName("리뷰 작성 실패: 이미 작성됨")
        void reviewFail1() {
            // given
            List<OrderLog> orderLogs = orderResult.getOrderLogs();
            CreateReviewDto dto = new CreateReviewDto(orderLogs.get(0).getIdx(), "리뷰 내용입니다.", 5);
            Review review = reviewService.saveReview(dto, user.getId());

            // when & then
            assertThatThrownBy(() -> reviewService.saveReview(dto, user.getId()))
                    .isInstanceOf(ReviewNotAllowedException.class)
                    .hasMessage("이미 리뷰를 작성한 주문입니다.");
        }

        @Test
        @DisplayName("리뷰 작성 실패: 배송 완료 상태가 아님")
        void reviewFail2() {
            // given
            orderService.cancelOrders(orderResult.getOrders().getIdx());
            List<OrderLog> orderLogs = orderResult.getOrderLogs();
            CreateReviewDto dto = new CreateReviewDto(orderLogs.get(0).getIdx(), "리뷰 내용입니다.", 5);

            // when & then
            assertThatThrownBy(() -> reviewService.saveReview(dto, user.getId()))
                    .isInstanceOf(ReviewNotAllowedException.class)
                    .hasMessage("배송 완료 상태에서만 리뷰를 달 수 있습니다.");
        }

        @Test
        @DisplayName("리뷰 작성 실패: 해당 주문에 대한 사용자가 아님")
        void reviewFail3() {
            // given
            User newUser = createNewUser();

            orderService.cancelOrders(orderResult.getOrders().getIdx());
            List<OrderLog> orderLogs = orderResult.getOrderLogs();
            CreateReviewDto dto = new CreateReviewDto(orderLogs.get(0).getIdx(), "리뷰 내용입니다.", 5);

            // when & then
            assertThatThrownBy(() -> reviewService.saveReview(dto, newUser.getId()))
                    .isInstanceOf(ReviewNotAllowedException.class)
                    .hasMessage("해당 상품을 주문한 사용자가 아닙니다.");
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
        ItemDto itemDto = new ItemDto(itemName, categoryIdx, 10000, "상품의 설명입니다.");
        Map<String, Object> save = itemService.save(itemDto, null, user);
        return (Item) save.get("item");
    }

    // 주문 생성
    private OrderResult createOrder(List<CreateOrdersDto> ordersList) {
        return orderService.saveOrders(user, ordersList);
    }
}