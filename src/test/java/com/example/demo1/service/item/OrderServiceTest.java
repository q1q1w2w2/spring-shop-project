package com.example.demo1.service.item;

import com.example.demo1.dto.item.CategoryResponseDto;
import com.example.demo1.dto.item.SaveItemResponseDto;
import com.example.demo1.entity.item.Category;
import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.item.OrderLog;
import com.example.demo1.entity.item.Orders;
import com.example.demo1.entity.user.User;
import com.example.demo1.dto.item.ItemDto;
import com.example.demo1.dto.order.CreateOrdersDto;
import com.example.demo1.dto.order.OrderResult;
import com.example.demo1.dto.user.JoinDto;
import com.example.demo1.exception.Item.item.ItemAlreadyDeleteException;
import com.example.demo1.exception.Item.item.ItemNotFoundException;
import com.example.demo1.exception.order.InvalidQuantityException;
import com.example.demo1.exception.order.OrderStepException;
import com.example.demo1.repository.item.CategoryRepository;
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
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired UserService userService;
    @Autowired ItemService itemService;
    @Autowired CategoryService categoryService;
    @Autowired OrderLogService orderLogService;

    private final int ORDER_COMP = 1;
    private final int START_DELIVERY = 2;
    private final int COMP_DELIVERY = 3;
    private final int ORDER_CANCEL = 10;

    private User user;
    private Category category;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        user = createUser();
        category = createCategory();
    }

    @Nested
    @DisplayName("주문 테스트")
    class OrderTest {

        @Test
        @DisplayName("주문 성공: 단일 상품 주문")
        void orderSuccess1() {
            // given
            Item item = createItem("상품1", category.getIdx(), user);
            final int quantity1 = 100;

            // when
            // 상품1 100개 주문 생성
            ArrayList<CreateOrdersDto> ordersList = new ArrayList<>();
            ordersList.add(new CreateOrdersDto(item.getIdx(), quantity1));
            OrderResult orderResult = createOrder(ordersList);

            Orders saveOrders = orderResult.getOrders();
            List<OrderLog> saveOrderLogs = orderResult.getOrderLogs();

//            List<Orders> findOrderList = orderService.findAll(new OrderSearch(), user);

            // then
//            assertThat(findOrderList.size()).isEqualTo(1); // 정상적으로 주문처리가 되었는가
            assertThat(saveOrderLogs.size()).isEqualTo(1); // 상품 종류 수만큼 orderLog가 생성되었는가
            // 주문 수량 검증
            assertThat(saveOrderLogs.get(0).getEa()).isEqualTo(quantity1);
            // 주문 상품 검증
            assertThat(saveOrderLogs.get(0).getItem().getIdx()).isEqualTo(item.getIdx());
            // 주문 상태 검증
            assertThat(saveOrders.getStep()).isEqualTo(ORDER_COMP);
            // 주문자 검증
            assertThat(saveOrders.getUser().getId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("주문 성공: 복수 상품 주문")
        void orderSuccess2() {
            // given
            Item item1 = createItem("상품1", category.getIdx(), user);
            Item item2 = createItem("상품2", category.getIdx(), user);
            final int quantity1 = 100;
            final int quantity2 = 200;

            // when
            // 상품1 100개, 상품2 200개 주문 생성
            ArrayList<CreateOrdersDto> ordersList = new ArrayList<>();
            ordersList.add(new CreateOrdersDto(item1.getIdx(), quantity1));
            ordersList.add(new CreateOrdersDto(item2.getIdx(), quantity2));
            OrderResult orderResult = createOrder(ordersList);

            Orders saveOrders = orderResult.getOrders();
            List<OrderLog> saveOrderLogs = orderResult.getOrderLogs();

//            List<Orders> findOrderList = orderService.findAll(new OrderSearch(), user);

            // then
//            assertThat(findOrderList.size()).isEqualTo(1);
            assertThat(saveOrderLogs.size()).isEqualTo(2);
            // 주문 수량 검증
            assertThat(saveOrderLogs.get(0).getEa()).isEqualTo(quantity1);
            assertThat(saveOrderLogs.get(1).getEa()).isEqualTo(quantity2);
            // 주문 상품 검증
            assertThat(saveOrderLogs.get(0).getItem().getIdx()).isEqualTo(item1.getIdx());
            assertThat(saveOrderLogs.get(1).getItem().getIdx()).isEqualTo(item2.getIdx());
            // 주문 상태 검증
            assertThat(saveOrders.getStep()).isEqualTo(ORDER_COMP);
            // 주문자 검증
            assertThat(saveOrders.getUser().getId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("주문 실패: 존재하지 않는 상품")
        void orderFail1() {
            // given

            // when & then
            ArrayList<CreateOrdersDto> list = new ArrayList<>();
            list.add(new CreateOrdersDto(1L, 100));

            assertThatThrownBy(() -> createOrder(list))
                    .isInstanceOf(ItemNotFoundException.class);
        }

        @Test
        @DisplayName("주문 실패: 삭제된 상품을 주문")
        void orderFail2() {
            // given
            Item item = createItem("상품1", category.getIdx(), user);
            itemService.delete(item.getIdx(), user);

            // when & then
            ArrayList<CreateOrdersDto> list = new ArrayList<>();
            list.add(new CreateOrdersDto(item.getIdx(), 100));

            assertThatThrownBy(() -> createOrder(list))
                    .isInstanceOf(ItemAlreadyDeleteException.class);
        }

        @Test
        @DisplayName("주문 실패: 수량이 1 미만")
        void orderFail3() {
            // given
            Item item = createItem("상품1", category.getIdx(), user);

            // when & then
            ArrayList<CreateOrdersDto> list = new ArrayList<>();
            list.add(new CreateOrdersDto(item.getIdx(), -1));

            assertThatThrownBy(() -> createOrder(list))
                    .isInstanceOf(InvalidQuantityException.class);
        }
    }

    @Nested
    @DisplayName("주문 상태 변경")
    class OrderStepTest {

        private Item item;
        private OrderResult orderResult;

        @BeforeEach
        void setUp() {
            // 상품 생성 및 주문 생성
            item = createItem("상품1", category.getIdx(), user);
            List<CreateOrdersDto> list = new ArrayList<>();
            list.add(new CreateOrdersDto(item.getIdx(), 100));
            orderResult = createOrder(list);
        }

        @Test
        @DisplayName("배송 시작 상태로 변경")
        void startDeliverySuccess() {
            // given
            Orders orders = orderResult.getOrders();

            // when
            orderService.startOrders(orders.getIdx());

            // then
            assertThat(orders.getStep()).isEqualTo(START_DELIVERY);
        }

        @Test
        @DisplayName("배송 완료 상태로 변경")
        void compDeliverySuccess() {
            // given
            Orders orders = orderResult.getOrders();

            // when
            orderService.completeOrders(orders.getIdx());

            // then
            assertThat(orders.getStep()).isEqualTo(COMP_DELIVERY);
        }

        @Test
        @DisplayName("주문 취소 상태로 변경")
        void cancelOrderSuccess() {
            // given
            Orders orders = orderResult.getOrders();

            // when
            orderService.cancelOrders(orders.getIdx());

            // then
            assertThat(orders.getStep()).isEqualTo(ORDER_CANCEL);
        }

        @Test
        @DisplayName("배송 시작 상태로 변경 실패: 이미 해당 상태")
        void compDeliveryFail() {
            // given
            Orders orders = orderResult.getOrders();
            orderService.completeOrders(orders.getIdx());

            // when & then
            assertThatThrownBy(() -> orderService.completeOrders(orders.getIdx()))
                    .isInstanceOf(OrderStepException.class);
        }

        @Test
        @DisplayName("배송 완료 상태로 변경 실패: 이미 해당 상태")
        void startDeliveryFail() {
            // given
            Orders orders = orderResult.getOrders();
            orderService.startOrders(orders.getIdx());

            // when & then
            assertThatThrownBy(() -> orderService.startOrders(orders.getIdx()))
                    .isInstanceOf(OrderStepException.class);
        }

        @Test
        @DisplayName("주문 취소 상태로 변경 실패: 이미 해당 상태")
        void cancelOrderFail() {
            // given
            Orders orders = orderResult.getOrders();
            orderService.cancelOrders(orders.getIdx());

            // when & then
            assertThatThrownBy(() -> orderService.cancelOrders(orders.getIdx()))
                    .isInstanceOf(OrderStepException.class);
        }
    }


    // 사용자 회원가입
    User createUser() {
        JoinDto joinDto = JoinDto.builder()
                .username("testUser")
                .loginId("testId")
                .password("testPassword")
                .birth(LocalDate.now())
                .tel("010-0000-0001")
                .address("서울시")
                .detail("1층")
                .build();
        return userService.join(joinDto);
    }

    // 카테고리 등록
    Category createCategory() {
        CategoryResponseDto categoryResponseDto = categoryService.save("카테고리1");
        return categoryRepository.findById(categoryResponseDto.getCategoryIdx())
                .orElseThrow();
    }

    // 상품 등록
    Item createItem(String itemName, Long categoryIdx, User user) {
        ItemDto itemDto = new ItemDto(itemName, categoryIdx, 10000, "상품의 설명입니다.");
        SaveItemResponseDto savedResult = itemService.save(itemDto, null, user);
        return savedResult.getItem();
    }

    // 주문 생성
    private OrderResult createOrder(List<CreateOrdersDto> ordersList) {
        return orderService.saveOrders(user, ordersList);
    }
}