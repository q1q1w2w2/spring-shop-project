package com.example.demo1.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LogoutDto {

    @NotNull
    private String accessToken;

    @NotNull
    private String refreshToken;
}
