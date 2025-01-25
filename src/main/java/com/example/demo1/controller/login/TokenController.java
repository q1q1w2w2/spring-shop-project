package com.example.demo1.controller.login;

import com.example.demo1.dto.auth.TokensDto;
import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.dto.user.RefreshTokenDto;
import com.example.demo1.util.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final TokenProvider tokenProvider;

    @PostMapping("/api/token-refresh")
    public ResponseEntity<Object> refresh(@Validated @RequestBody RefreshTokenDto request) throws Exception {
        String refreshToken = request.getRefreshToken();

        if (!tokenProvider.validateToken(refreshToken)) {
            ApiResponse<Object> response = ApiResponse.error(UNAUTHORIZED, "토큰이 유효하지 않습니다.");
            return ResponseEntity.status(UNAUTHORIZED).body(response);
        }

        String subject = tokenProvider.extractUserIdFromRefreshToken(refreshToken);
        String accessToken = tokenProvider.createNewAccessToken(refreshToken);
        String newRefreshToken = tokenProvider.createRefreshToken(subject);

        TokensDto tokensDto = new TokensDto(accessToken, newRefreshToken);

        ApiResponse<TokensDto> response = ApiResponse.success(OK, "토큰이 재발급되었습니다.", tokensDto);
        return ResponseEntity.ok(response);
    }

}
