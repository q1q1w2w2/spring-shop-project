package com.example.demo1.controller.item;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.dto.item.AddItemCartDto;
import com.example.demo1.dto.item.ItemCartEaUpdateDto;
import com.example.demo1.dto.item.ItemCartResponseDto;
import com.example.demo1.service.facade.CartFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo1.util.common.ApiResponseUtil.createResponse;
import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
public class ItemCartController {

    private final CartFacade cartFacade;

    @PostMapping("/api/cart")
    public ResponseEntity<ApiResponse<ItemCartResponseDto>> addItemCart(@RequestBody AddItemCartDto itemCartDto) {
        ItemCartResponseDto savedItemCart = cartFacade.save(itemCartDto);
        return createResponse(OK, "상품이 장바구니에 담겼습니다.", savedItemCart);
    }

    @PatchMapping("/api/cart/update")
    public ResponseEntity<ApiResponse<ItemCartResponseDto>> updateCart(@RequestBody ItemCartEaUpdateDto dto) {
        ItemCartResponseDto updatedItemCart = cartFacade.updateCart(dto);
        return createResponse(OK, "수량이 업데이트 되었습니다.", updatedItemCart);
    }

    @PatchMapping("/api/cart/delete/{cartIdx}")
    public ResponseEntity<ApiResponse<Object>> deleteCart(@PathVariable Long cartIdx) {
        cartFacade.deleteCart(cartIdx);
        return createResponse(OK, "장바구니에서 삭제되었습니다.");
    }

    @PatchMapping("/api/cart/order")
    public ResponseEntity<ApiResponse<Object>> orderCart() {
        cartFacade.orderCart();
        return createResponse(OK, "주문이 완료되었습니다.");
    }

    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<List<ItemCartResponseDto>>> getCart() {
        List<ItemCartResponseDto> cartList = cartFacade.getCart();
        return createResponse(OK, cartList);
    }

    @PatchMapping("/cart/empty")
    public ResponseEntity<ApiResponse<Object>> emptyCart() {
        cartFacade.clearCart();
        return createResponse(OK, "장바구니를 비웠습니다.");
    }
}
