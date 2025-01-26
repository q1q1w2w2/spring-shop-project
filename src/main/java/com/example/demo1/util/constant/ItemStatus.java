package com.example.demo1.util.constant;

public enum ItemStatus {
    ACTIVATED(0)
    , DELETED(1);

    private int value;

    ItemStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
