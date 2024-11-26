package com.example.demo1.exception.token;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException() {
        super();
    }

    public TokenValidationException(String message) {
        super(message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenValidationException(Throwable cause) {
        super(cause);
    }

    protected TokenValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
