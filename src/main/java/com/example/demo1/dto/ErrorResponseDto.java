package com.example.demo1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status; // 에러 코드
    private final String error; // 에러 명
    private final String message;
}
