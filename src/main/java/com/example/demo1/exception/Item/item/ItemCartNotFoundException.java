package com.example.demo1.exception.Item.item;

public class ItemCartNotFoundException extends RuntimeException {
    public ItemCartNotFoundException() {
        super("장바구니를 찾을 수 없습니다.");
    }
}
