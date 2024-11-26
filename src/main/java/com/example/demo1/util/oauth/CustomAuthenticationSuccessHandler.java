package com.example.demo1.util.oauth;

import com.example.demo1.libs.auth.Auth;
import com.example.demo1.util.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    // OAuth 인증 성공 후 처리를 위한 클래스

    //    private final TokenProvider tokenProvider;
    private final Auth auth;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("[AuthenticationSuccessHandler 실행]");
/**
        // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2CustomUser oAuth2User = (OAuth2CustomUser) authentication.getPrincipal();
        log.info(authentication.getPrincipal().toString());

        // 사용자 이메일을 가져온다.
        String email = oAuth2User.getEmail();
        // 서비스 제공 플랫폼
        String provider = oAuth2User.getName();

        // OAuth2User로 부터 Role을 얻어온다.
        String role = oAuth2User.getAuthorities().stream()
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAuthority();

        log.info("email: {}, provider: {}, role: {}", email, provider, role);

        // 회원이 존재하면 jwt token 발행을 시작한다.
        String accessToken = null;
        String refreshToken = null;
        try {
            accessToken = tokenProvider.createAccessToken(email, role);
            refreshToken = tokenProvider.createRefreshToken(email);

        } catch (Exception e) {
            throw new RuntimeException("토큰 생성 실패");
        }

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("accessToken", accessToken);
        responseMap.put("refreshToken", refreshToken);

        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(), responseMap);
**/
    }
}
