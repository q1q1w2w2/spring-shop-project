package com.example.demo1.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokensDto {

    private String accessToken;
    private String refreshToken;
}
