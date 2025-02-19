package com.example.demo1.util.common;

import com.example.demo1.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ApiResponseUtil {
    public static <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status) {
        ApiResponse<T> response = ApiResponse.success(status);
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, T data) {
        ApiResponse<T> response = ApiResponse.success(status, data);
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, String message) {
        ApiResponse<T> response = ApiResponse.success(status, message);
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, String message, T data) {
        ApiResponse<T> response = ApiResponse.success(status, message, data);
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<ApiResponse<Object>> createErrorResponse(Exception e, HttpStatus status, String message) {
        log.error("[{}] 발생: {}", e.getClass().getSimpleName(), message);
        ApiResponse<Object> response = ApiResponse.error(status, message);
        return ResponseEntity.status(status).body(response);
    }
}
