package com.example.demo1.service.item;

import com.example.demo1.dto.item.ItemCartResponseDto;
import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.item.ItemCart;
import com.example.demo1.entity.user.User;
import com.example.demo1.dto.item.AddItemCartDto;
import com.example.demo1.dto.item.ItemCartEaUpdateDto;
import com.example.demo1.exception.Item.item.ItemCartNotFoundException;
import com.example.demo1.exception.Item.item.ItemNotFoundException;
import com.example.demo1.repository.item.ItemCartRepository;
import com.example.demo1.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.demo1.util.constant.CartStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemCartService {

    private final ItemCartRepository itemCartRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemCart save(AddItemCartDto dto, User user) {
        Optional<ItemCart> findItemCart = itemCartRepository.findByUserIdAndItemIdxAndStatusZero(user.getId(), dto.getItemIdx());
        if (findItemCart.isPresent()) {
            ItemCart itemCart = findItemCart.get();
            itemCart.updateEa(itemCart.getEa() + dto.getEa());
            return itemCart;
        }

        Item item = itemRepository.findById(dto.getItemIdx())
                .orElseThrow(ItemNotFoundException::new);
        ItemCart itemCart = ItemCart.builder()
                .user(user)
                .item(item)
                .ea(dto.getEa())
                .build();
        return itemCartRepository.save(itemCart);
    }

    @Transactional
    public ItemCart updateEa(ItemCartEaUpdateDto dto, User user) {
        ItemCart itemCart = itemCartRepository.findById(dto.getCartIdx())
                .orElseThrow(ItemNotFoundException::new);
        itemCart.updateEa(dto.getEa());
        return itemCart;
    }

    public List<ItemCartResponseDto> getCartDto(User user) {
        List<ItemCart> itemCarts = itemCartRepository.findByUserAndStatusIsLike(user, CART_ADD.getValue())
                .orElseThrow(ItemCartNotFoundException::new);

        List<ItemCartResponseDto> itemCartDtoList = new ArrayList<>();
        for (ItemCart itemCart : itemCarts) {
            itemCartDtoList.add(new ItemCartResponseDto(itemCart));
        }
        return itemCartDtoList;
    }

    public List<ItemCart> getCart(User user) {
        return itemCartRepository.findByUserAndStatusIsLike(user, CART_ADD.getValue())
                .orElseThrow(ItemCartNotFoundException::new);
    }

    @Transactional
    public void deleteCart(Long cartIdx, User user) {
        ItemCart itemCart = itemCartRepository.findById(cartIdx)
                .orElseThrow(ItemNotFoundException::new);
        itemCart.updateStatus(CART_DELETED.getValue());
    }

    @Transactional
    public void orderCart(Long itemCartIdx, User user) {
        ItemCart itemCart = itemCartRepository.findById(itemCartIdx)
                .orElseThrow(ItemNotFoundException::new);
        itemCart.updateStatus(CART_COMP.getValue());
    }

    @Transactional
    public void clearCart(User user) {
        List<ItemCart> itemCarts = itemCartRepository.findByUserAndStatusIsLike(user, CART_ADD.getValue())
                .orElseThrow(ItemCartNotFoundException::new);
        for (ItemCart itemCart : itemCarts) {
            itemCart.updateStatus(CART_DELETED.getValue());
        }
    }
}
