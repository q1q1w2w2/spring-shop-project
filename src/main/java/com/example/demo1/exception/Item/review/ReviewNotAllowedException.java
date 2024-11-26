package com.example.demo1.exception.Item.review;

public class ReviewNotAllowedException extends RuntimeException {
    public ReviewNotAllowedException() {
    }

    public ReviewNotAllowedException(String message) {
        super(message);
    }
}
