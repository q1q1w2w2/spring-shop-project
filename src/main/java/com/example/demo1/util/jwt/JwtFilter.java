package com.example.demo1.util.jwt;

import com.example.demo1.libs.auth.Auth;
import com.example.demo1.libs.auth.JwtProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final Auth auth;
    public final static String AUTHORIZATION_HEADER = "Authorization";
    public static String TOKEN_PREFIX;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 경로 가져오기
        String requestUrl = request.getRequestURI();
        // 요청 메서드 가져오기
        String method = request.getMethod();
//        System.out.println("요청 URL : "+requestUrl);
        // 특정 경로는 필터 적용하지 않도록 처리
        // ⚠️ h2 콘솔 조건 추가
        AntPathMatcher pathMatcher = new AntPathMatcher();

        // 특정 경로는 필터 적용하지 않도록 처리
        if (pathMatcher.match("/h2-console/**", requestUrl)
                || pathMatcher.match("/favicon.ico", requestUrl)
                || pathMatcher.match("/api/public/**", requestUrl)
                || pathMatcher.match("/api/login", requestUrl)
                || pathMatcher.match("/api/join", requestUrl)
                || pathMatcher.match("/item/list", requestUrl)
                || pathMatcher.match("/item", requestUrl)
                || pathMatcher.match("/api/category", requestUrl)
                || pathMatcher.match("/item/review", requestUrl)
                || pathMatcher.match("/login", requestUrl)
                || pathMatcher.match("/itemList.html", requestUrl)
                || pathMatcher.match("/api/mailSend", requestUrl)
                || pathMatcher.match("/api/mailCode/valid", requestUrl)
                || pathMatcher.match("/api/user/sendMail", requestUrl)
                || pathMatcher.match("/api/pwd-reset", requestUrl)
        ) {
            filterChain.doFilter(request,response); // 필터를 생략하고 다음 필터로 넘어감
            return;
        }

        // 요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        // 가져온 값에서 접두사 제거 ( Hwannee )
        String token = getAccessToken(authorizationHeader);
        log.info("token: {}", token);
        // 가져온 토큰이 유효한지 확인 후, 유효한 때는 인증 정보 설정
        try{
            // 토큰이 유효하면 Authentication 객체 생성 후 SecurityContext에 저장
            Authentication authentication = auth.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (IllegalArgumentException e){
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            Map<String, Object> res = new HashMap<>();
            res.put("message", "잘못된 요청입니다.");
            res.put("Reason", "Bad Request");
            res.put("path", request.getRequestURL());

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), res);
        }

        // 다음 필터로 넘어감
        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader){
        TOKEN_PREFIX = jwtProperties.getPrefix();

        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)){
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }



    /** extends GenericFilterBean 사용했었음 ->
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("JWT 인증 정보 Security Context에 저장");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();
        log.info("jwt: {}", jwt);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = null;
            try {
                authentication = tokenProvider.getAuthentication(jwt);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보 저장, uri: {}", authentication.getName(), requestURI);
        } else {
            log.info("유효한 JWT 없음, uri: {}", requestURI);
        }

        chain.doFilter(request, response);
    }

    public String resolveToken(HttpServletRequest request) {
        log.info("header에서 토큰 꺼냄");
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
    **/
}
