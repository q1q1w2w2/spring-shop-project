package com.example.demo1.exception.Item.itemImage;

public class InvalidImageSequenceException extends RuntimeException {
    public InvalidImageSequenceException() {
        super();
    }

    public InvalidImageSequenceException(String message) {
        super(message);
    }
}
