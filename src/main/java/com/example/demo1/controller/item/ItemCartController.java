package com.example.demo1.controller.item;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.entity.item.ItemCart;
import com.example.demo1.entity.user.User;
import com.example.demo1.dto.item.ItemCartAddDto;
import com.example.demo1.dto.item.ItemCartEaUpdateDto;
import com.example.demo1.dto.item.ItemCartResponseDto;
import com.example.demo1.dto.order.CreateOrdersDto;
import com.example.demo1.exception.Item.item.ItemCartNotFoundException;
import com.example.demo1.service.item.ItemCartService;
import com.example.demo1.service.item.OrderService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
public class ItemCartController {

    private final ItemCartService itemCartService;
    private final UserService userService;
    private final OrderService orderService;

    @PostMapping("/api/cart")
    public ResponseEntity<ApiResponse<ItemCartResponseDto>> addItemCart(@RequestBody ItemCartAddDto dto) {
        User user = userService.getCurrentUser();
        ItemCart itemCart = itemCartService.save(dto, user);
        ItemCartResponseDto responseDto = new ItemCartResponseDto(itemCart);

        ApiResponse<ItemCartResponseDto> response = ApiResponse.success(OK, "상품이 장바구니에 담겼습니다.", responseDto);
        return ResponseEntity.status(OK).body(response);
    }

    @PatchMapping("/api/cart/update")
    public ResponseEntity<ApiResponse<ItemCartResponseDto>> updateCart(@RequestBody ItemCartEaUpdateDto dto) {
        User user = userService.getCurrentUser();
        ItemCart itemCart = itemCartService.updateEa(dto, user);

        ApiResponse<ItemCartResponseDto> response = ApiResponse.success(OK, "수량이 업데이트 되었습니다.", new ItemCartResponseDto(itemCart));
        return ResponseEntity.status(OK).body(response);
    }

    @PatchMapping("/api/cart/delete/{cartIdx}")
    public ResponseEntity<ApiResponse<Object>> deleteCart(@PathVariable Long cartIdx) {
        User user = userService.getCurrentUser();
        itemCartService.deleteCart(cartIdx, user);

        ApiResponse<Object> response = ApiResponse.success(OK, "장바구니에서 제거되었습니다.");
        return ResponseEntity.status(OK).body(response);
    }

    @PatchMapping("/api/cart/order")
    public ResponseEntity<ApiResponse<Object>> orderCart() {
        User user = userService.getCurrentUser();

        List<CreateOrdersDto> orderList = new ArrayList<>();
        List<ItemCart> cartList = itemCartService.getCart(user);
        for (ItemCart itemCart : cartList) {
            orderList.add(new CreateOrdersDto(itemCart.getItem().getIdx(), itemCart.getEa()));
            itemCartService.orderCart(itemCart.getIdx(), user);
        }
        orderService.saveOrders(user, orderList);

        ApiResponse<Object> response = ApiResponse.success(OK, "주문이 완료되었습니다.");
        return ResponseEntity.status(OK).body(response);
    }

    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<List<ItemCartResponseDto>>> getCart() {
        User user = userService.getCurrentUser();
        List<ItemCartResponseDto> cartList = itemCartService.getCartDto(user);

        ApiResponse<List<ItemCartResponseDto>> response = ApiResponse.success(OK, cartList);
        return ResponseEntity.status(OK).body(response);
    }

    @PatchMapping("/cart/empty")
    public ResponseEntity<ApiResponse<Object>> emptyCart() {
        User user = userService.getCurrentUser();
        itemCartService.emptyCart(user);

        ApiResponse<Object> response = ApiResponse.success(OK, "장바구니를 비웠습니다.");
        return ResponseEntity.status(OK).body(response);
    }
}
