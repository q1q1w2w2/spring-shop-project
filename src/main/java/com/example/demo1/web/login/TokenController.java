package com.example.demo1.web.login;

import com.example.demo1.domain.user.User;
import com.example.demo1.dto.user.RefreshTokenDto;
import com.example.demo1.libs.auth.Auth;
import com.example.demo1.libs.auth.TokenRequest;
import com.example.demo1.service.login.RefreshTokenService;
import com.example.demo1.service.user.UserService;
import com.example.demo1.util.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenController {

//    private final TokenProvider tokenProvider;
    private final Auth auth;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    // 토큰 재발급(Auth lib 사용)
    @PostMapping("/api/token-refresh")
    public ResponseEntity<Map<String, String>> refresh(@Validated @RequestBody RefreshTokenDto request) {
        String refreshToken = request.getRefreshToken();

        // refresh token이 유효하지 않을 경우
        TokenRequest tokenRequest = auth.verifyToken(refreshToken);
        User user = userService.findById(tokenRequest.getIdx());

        // token 새롭게 발급 + 이전 refresh token 무효화
        String accessToken = auth.generateToken(user, Duration.ofHours(2));
        String newRefreshToken = auth.generateToken(user, Duration.ofHours(14));

        // todo redis 사용하기
        refreshTokenService.saveRefreshToken(newRefreshToken, user.getLoginId());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", newRefreshToken
        ));
    }

    // 토큰 재발급
//    @PostMapping("/api/token-refresh")
//    public ResponseEntity<Map<String, String>> refresh(@Validated @RequestBody RefreshTokenDto request) throws Exception {
//        String refreshToken = request.getRefreshToken();
//
//        // refresh token이 유효하지 않을 경우
//        if (!tokenProvider.validateToken(refreshToken)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "refresh token이 유효하지 않습니다."));
//        }
//
//        String subject = tokenProvider.extractUserIdFromRefreshToken(refreshToken);
//        // token 새롭게 발급 + 이전 refresh token 무효화
//        String accessToken = tokenProvider.createNewAccessToken(refreshToken);
//        String newRefreshToken = tokenProvider.createRefreshToken(subject);
//
//        return ResponseEntity.ok(Map.of(
//                "accessToken", accessToken,
//                "refreshToken", newRefreshToken
//        ));
//    }
}
