package com.example.demo1.util.oauth;

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

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("[AuthenticationSuccessHandler 실행]");

        OAuth2CustomUser oAuth2User = (OAuth2CustomUser) authentication.getPrincipal();
        log.info(authentication.getPrincipal().toString());

        String email = oAuth2User.getEmail();
        String provider = oAuth2User.getName();

        String role = oAuth2User.getAuthorities().stream()
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAuthority();

        log.info("email: {}, provider: {}, role: {}", email, provider, role);

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

    }
}
