package com.example.demo1.web.item;

import com.example.demo1.domain.item.ItemCart;
import com.example.demo1.domain.user.User;
import com.example.demo1.dto.item.ItemCartAddDto;
import com.example.demo1.dto.item.ItemCartEaUpdateDto;
import com.example.demo1.dto.item.ItemCartResponseDto;
import com.example.demo1.dto.item.ItemCartStatusUpdateDto;
import com.example.demo1.dto.order.CreateOrdersDto;
import com.example.demo1.dto.order.OrderResult;
import com.example.demo1.service.item.ItemCartService;
import com.example.demo1.service.item.OrderService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ItemCartController {

    private final ItemCartService itemCartService;
    private final UserService userService;
    private final OrderService orderService;

    // 장바구니에 추가(상품idx, 수량, useridx)
    @PostMapping("/api/cart")
    public ResponseEntity<ItemCartResponseDto> addItemCart(@RequestBody ItemCartAddDto dto) {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
        ItemCart itemCart = itemCartService.save(dto, user);
        ItemCartResponseDto response = new ItemCartResponseDto(itemCart);
        return ResponseEntity.ok(response);
    }

    // 장바구니 수정(수량)
    @PatchMapping("/api/cart/update")
    public ResponseEntity<Map<String, Object>> updateCart(@RequestBody ItemCartEaUpdateDto dto) {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
        ItemCart itemCart = itemCartService.updateEa(dto, user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "수량이 업데이트 되었습니다.");
        response.put("itemCart", new ItemCartResponseDto(itemCart));
        return ResponseEntity.ok(response);
    }

    // 장바구니 제거(status -> 1)
    @PatchMapping("/api/cart/delete/{cartIdx}")
    public ResponseEntity<Map<String, String>> deleteCart(@PathVariable Long cartIdx) {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
        itemCartService.deleteCart(cartIdx, user);
        return ResponseEntity.ok(Map.of("message", "장바구니에서 제거되었습니다."));
    }

    // 장바구니 주문(status -> 2)
    @PatchMapping("/api/cart/order")
    public ResponseEntity<Map<String, String>> orderCart() {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());

        List<CreateOrdersDto> orderList = new ArrayList<>();
        List<ItemCart> cartList = itemCartService.getCart(user);
        if (cartList == null || cartList.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "장바구니가 비어있습니다."));
        }
        for (ItemCart itemCart : cartList) {
            orderList.add(new CreateOrdersDto(itemCart.getItem().getIdx(), itemCart.getEa()));
            // 상태 변경
            itemCartService.orderCart(itemCart.getIdx(), user);
        }
        // 주문
        orderService.saveOrders(user, orderList);

        return ResponseEntity.ok(Map.of("message", "주문이 완료되었습니다."));
    }

    // 장바구니 불러오기
    @GetMapping("/cart")
    public ResponseEntity<List<ItemCartResponseDto>> getCart() {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
        List<ItemCart> cartList = itemCartService.getCart(user);

        List<ItemCartResponseDto> response = new ArrayList<>();
        for (ItemCart itemCart : cartList) {
            response.add(new ItemCartResponseDto(itemCart));
        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cart/empty")
    public ResponseEntity<Map<String, String>> emptyCart() {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
        itemCartService.emptyCart(user);
        return ResponseEntity.ok(Map.of("message", "장바구니를 비웠습니다."));
    }
}
