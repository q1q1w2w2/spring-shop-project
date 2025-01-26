package com.example.demo1.util.constant;

public enum OrderStep {
    ORDER_COMP(1),
    ORDER_START(2),
    DELIVERY_COMP(3),
    ORDER_CANCEL(10);

    private final int value;

    OrderStep(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
