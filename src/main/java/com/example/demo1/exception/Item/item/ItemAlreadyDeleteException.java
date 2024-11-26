package com.example.demo1.exception.Item.item;

public class ItemAlreadyDeleteException extends RuntimeException {
    public ItemAlreadyDeleteException() {
        super();
    }

    public ItemAlreadyDeleteException(String message) {
        super(message);
    }
}
