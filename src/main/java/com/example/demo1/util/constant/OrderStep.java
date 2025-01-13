package com.example.demo1.util.constant;

public enum OrderStep {
    ORDER_START(2),
    ORDER_COMP(3),
    ORDER_CANCEL(10);

    private final int value;

    OrderStep(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
