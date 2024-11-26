package com.example.demo1.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {

    @NotNull(message = "refresh token은 비어있을 수 없습니다.")
    private String refreshToken;


}
