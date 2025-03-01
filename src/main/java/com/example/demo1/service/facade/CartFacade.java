package com.example.demo1.service.facade;

import com.example.demo1.dto.item.AddItemCartDto;
import com.example.demo1.dto.item.ItemCartEaUpdateDto;
import com.example.demo1.dto.item.ItemCartResponseDto;
import com.example.demo1.dto.order.CreateOrdersDto;
import com.example.demo1.entity.item.ItemCart;
import com.example.demo1.entity.user.User;
import com.example.demo1.service.item.ItemCartService;
import com.example.demo1.service.item.OrderService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartFacade {

    private final ItemCartService itemCartService;
    private final UserService userService;
    private final OrderService orderService;

    @Transactional
    public ItemCartResponseDto save(AddItemCartDto itemCartDto) {
        ItemCart savedItemCart = itemCartService.save(itemCartDto, getCurrentUser());
        return new ItemCartResponseDto(savedItemCart);
    }

    @Transactional
    public ItemCartResponseDto updateCart(ItemCartEaUpdateDto itemCartUpdateDto) {
        ItemCart updatedItemCart = itemCartService.updateEa(itemCartUpdateDto, getCurrentUser());
        return new ItemCartResponseDto(updatedItemCart);
    }

    @Transactional
    public void deleteCart(Long cartIdx) {
        itemCartService.deleteCart(cartIdx, getCurrentUser());
    }

    @Transactional
    public void orderCart() {
        User user = getCurrentUser();

        List<ItemCart> cartList = itemCartService.getCart(user);
        List<CreateOrdersDto> orderList = cartList.stream()
                .map(cart -> new CreateOrdersDto(cart.getItem().getIdx(), cart.getEa()))
                .toList();

        itemCartService.bulkOrderCart(orderList);
        orderService.saveOrders(user, orderList);
    }

    public List<ItemCartResponseDto> getCart() {
        return itemCartService.getCartDto(getCurrentUser());
    }

    @Transactional
    public void clearCart() {
        itemCartService.clearCart(getCurrentUser());
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }
}
