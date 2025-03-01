package com.example.demo1.controller.login;

import com.example.demo1.dto.auth.TokensDto;
import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.dto.user.RefreshTokenDto;
import com.example.demo1.service.login.TokenService;
import com.example.demo1.util.common.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/api/token-refresh")
    public ResponseEntity<ApiResponse<TokensDto>> refresh(@Valid @RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        TokensDto tokensDto = tokenService.refreshTokens(refreshTokenDto);
        return ApiResponseUtil.createResponse(OK, "토큰이 재발급되었습니다.", tokensDto);
    }

}
