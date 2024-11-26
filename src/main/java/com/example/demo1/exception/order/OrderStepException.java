package com.example.demo1.exception.order;

public class OrderStepException extends RuntimeException {
    public OrderStepException() {
        super();
    }

    public OrderStepException(String message) {
        super(message);
    }
}
