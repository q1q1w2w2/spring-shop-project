package com.example.demo1.util.constant;

public enum CartStatus {
    CART_ADD(0),
    CART_DELETED(1),
    CART_COMP(2);

    private final int value;

    CartStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
