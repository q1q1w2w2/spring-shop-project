package com.example.demo1.controller.login;

import com.example.demo1.dto.user.RefreshTokenDto;
import com.example.demo1.util.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final TokenProvider tokenProvider;

    @PostMapping("/api/token-refresh")
    public ResponseEntity<Map<String, String>> refresh(@Validated @RequestBody RefreshTokenDto request) throws Exception {
        String refreshToken = request.getRefreshToken();

        if (!tokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "refresh token이 유효하지 않습니다."));
        }

        String subject = tokenProvider.extractUserIdFromRefreshToken(refreshToken);
        String accessToken = tokenProvider.createNewAccessToken(refreshToken);
        String newRefreshToken = tokenProvider.createRefreshToken(subject);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", newRefreshToken
        ));
    }

}
