package com.example.demo1.dto.item;

import com.example.demo1.domain.item.ItemCart;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCartResponseDto {

    private Long cartIdx;
    private String itemName;
    private int ea;
    private int itemPrice;
    private int totalPrice;
    private String imageUrl;


    public ItemCartResponseDto(ItemCart itemCart) {
        this.cartIdx = itemCart.getIdx();
        this.itemName = itemCart.getItem().getItemName();
        this.ea = itemCart.getEa();
        this.itemPrice = itemCart.getItem().getPrice();
        this.totalPrice = itemPrice * ea;
        this.imageUrl = itemCart.getItem().getImages().get(0).getImageUrl();
    }
}
