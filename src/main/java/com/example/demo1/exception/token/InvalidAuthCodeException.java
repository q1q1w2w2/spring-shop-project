package com.example.demo1.exception.token;

public class InvalidAuthCodeException extends RuntimeException {
    public InvalidAuthCodeException() {
        super("인증 코드가 일치하지 않습니다.");
    }
}
