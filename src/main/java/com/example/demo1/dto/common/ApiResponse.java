package com.example.demo1.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private static final String SUCCESS = "Success";

    private String status;
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(HttpStatus status) {
        return createResponse(status, SUCCESS, null);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return createResponse(status, SUCCESS, data);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, String message) {
        return createResponse(status, message, null);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        return createResponse(status, message, data);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return createResponse(status, message, null);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, T data) {
        return createResponse(status, message, data);
    }

    private static <T> ApiResponse<T> createResponse(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status.name(), status.value(), message, data);
    }
}
