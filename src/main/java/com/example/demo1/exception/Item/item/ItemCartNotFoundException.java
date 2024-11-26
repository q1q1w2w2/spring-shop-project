package com.example.demo1.exception.Item.item;

public class ItemCartNotFoundException extends RuntimeException {
    public ItemCartNotFoundException(String message) {
        super(message);
    }
}
