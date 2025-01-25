package com.example.demo1.controller.item;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.entity.item.ItemCart;
import com.example.demo1.entity.user.User;
import com.example.demo1.dto.item.AddItemCartDto;
import com.example.demo1.dto.item.ItemCartEaUpdateDto;
import com.example.demo1.dto.item.ItemCartResponseDto;
import com.example.demo1.service.facade.CartFacade;
import com.example.demo1.service.item.ItemCartService;
import com.example.demo1.service.item.OrderService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
public class ItemCartController {

    private final CartFacade cartFacade;

    @PostMapping("/api/cart")
    public ResponseEntity<ApiResponse<ItemCartResponseDto>> addItemCart(@RequestBody AddItemCartDto itemCartDto) {
        ItemCart itemCart = cartFacade.save(itemCartDto);

        ApiResponse<ItemCartResponseDto> response = ApiResponse.success(OK, "상품이 장바구니에 담겼습니다.", new ItemCartResponseDto(itemCart));
        return ResponseEntity.status(OK).body(response);
    }

    @PatchMapping("/api/cart/update")
    public ResponseEntity<ApiResponse<ItemCartResponseDto>> updateCart(@RequestBody ItemCartEaUpdateDto dto) {
        ItemCart itemCart = cartFacade.updateCart(dto);

        ApiResponse<ItemCartResponseDto> response = ApiResponse.success(OK, "수량이 업데이트 되었습니다.", new ItemCartResponseDto(itemCart));
        return ResponseEntity.status(OK).body(response);
    }

    @PatchMapping("/api/cart/delete/{cartIdx}")
    public ResponseEntity<ApiResponse<Object>> deleteCart(@PathVariable Long cartIdx) {
        cartFacade.deleteCart(cartIdx);

        ApiResponse<Object> response = ApiResponse.success(OK, "장바구니에서 제거되었습니다.");
        return ResponseEntity.status(OK).body(response);
    }

    @PatchMapping("/api/cart/order")
    public ResponseEntity<ApiResponse<Object>> orderCart() {
        cartFacade.orderCart();

        ApiResponse<Object> response = ApiResponse.success(OK, "주문이 완료되었습니다.");
        return ResponseEntity.status(OK).body(response);
    }

    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<List<ItemCartResponseDto>>> getCart() {
        List<ItemCartResponseDto> cartList = cartFacade.getCart();

        ApiResponse<List<ItemCartResponseDto>> response = ApiResponse.success(OK, cartList);
        return ResponseEntity.status(OK).body(response);
    }

    @PatchMapping("/cart/empty")
    public ResponseEntity<ApiResponse<Object>> emptyCart() {
        cartFacade.clearCart();

        ApiResponse<Object> response = ApiResponse.success(OK, "장바구니를 비웠습니다.");
        return ResponseEntity.status(OK).body(response);
    }
}
