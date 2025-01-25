package com.example.demo1.service.facade;

import com.example.demo1.dto.item.AddItemCartDto;
import com.example.demo1.dto.item.ItemCartEaUpdateDto;
import com.example.demo1.dto.item.ItemCartResponseDto;
import com.example.demo1.dto.item.ItemUpdateDto;
import com.example.demo1.dto.order.CreateOrdersDto;
import com.example.demo1.entity.item.ItemCart;
import com.example.demo1.entity.user.User;
import com.example.demo1.service.item.ItemCartService;
import com.example.demo1.service.item.OrderService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartFacade {

    private final ItemCartService itemCartService;
    private final UserService userService;
    private final OrderService orderService;

    @Transactional
    public ItemCart save(AddItemCartDto itemCartDto) {
        return itemCartService.save(itemCartDto, getCurrentUser());
    }

    @Transactional
    public ItemCart updateCart(ItemCartEaUpdateDto itemCartUpdateDto) {
        return itemCartService.updateEa(itemCartUpdateDto, getCurrentUser());
    }

    @Transactional
    public void deleteCart(Long cartIdx) {
        itemCartService.deleteCart(cartIdx, getCurrentUser());
    }

    @Transactional
    public void orderCart() {
        User user = getCurrentUser();

        List<CreateOrdersDto> orderList = new ArrayList<>();
        List<ItemCart> cartList = itemCartService.getCart(user);
        for (ItemCart itemCart : cartList) {
            orderList.add(new CreateOrdersDto(itemCart.getItem().getIdx(), itemCart.getEa()));
            itemCartService.orderCart(itemCart.getIdx(), user);
        }
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
