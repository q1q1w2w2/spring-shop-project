package com.example.demo1.controller.login;

import com.example.demo1.dto.auth.TokensDto;
import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.dto.user.LoginDto;
import com.example.demo1.dto.user.RefreshTokenDto;
import com.example.demo1.service.login.AuthService;
import com.example.demo1.util.common.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.example.demo1.util.common.ApiResponseUtil.*;
import static com.example.demo1.util.jwt.JwtFilter.*;
import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/api/login")
    public ResponseEntity<ApiResponse<TokensDto>> login(@Valid @RequestBody LoginDto dto) throws Exception {
        TokensDto tokens = authService.login(dto);
        return createResponse(OK, "로그인 되었습니다.", tokens);
    }

    @PostMapping("/api/logout")
    public ResponseEntity<ApiResponse<Object>> logout(
            @RequestHeader(AUTHORIZATION_HEADER) String authorization,
            @Valid @RequestBody RefreshTokenDto refreshTokenDto
    ) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        String accessToken = authorization.substring(TOKEN_PREFIX.length());
        authService.logout(refreshToken, accessToken);
        return createResponse(OK, "로그아웃 되었습니다.");
    }
}
