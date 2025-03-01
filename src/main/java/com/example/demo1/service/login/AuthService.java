package com.example.demo1.service.login;

import com.example.demo1.dto.auth.TokensDto;
import com.example.demo1.dto.user.LoginDto;
import com.example.demo1.exception.token.TokenValidationException;
import com.example.demo1.util.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo1.util.constant.Role.*;

/**
 * ** login 동작 **
 * Dto로 loginId, password를 받음
 * 받은 정보로 UsernamePasswordAuthenticationToken 생성
 * authenticationManager로 위의 token을 호출하여 인증 시도
 * authenticationManager는 내부적으로 UserDetailsService의 loadUserByUsername 호출하여 정보 조회
 * 사용자가 존재하면 UserDetails 반환, 없다면 UsernameNotFoundException
 * 요청으로 받은 password와 UserDetails의 password 비교(BadCredentialException)
 * 성공 시 Authentication 객체 반환
 * Authentication객체를 SecurityContext에 저장하고 jwt 생성
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "refreshToken:";

    @Transactional
    public TokensDto login(LoginDto dto) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getLoginId(), dto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String subject = authentication.getName();
        String authority = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(ROLE_USER.toString());

        String accessToken = tokenProvider.createAccessToken(subject, authority);
        String refreshToken = tokenProvider.createRefreshToken(subject);

        return new TokensDto(accessToken, refreshToken);
    }

    @Transactional
    public void logout(String refreshToken, String accessToken) throws Exception {
        if (!tokenProvider.validateToken(accessToken) || !tokenProvider.validateToken(refreshToken)) {
            throw new TokenValidationException("토큰이 유효하지 않습니다.");
        }
        String userId = tokenProvider.extractUserIdFromRefreshToken(refreshToken);
        String redisKey = REDIS_KEY_PREFIX + userId;

        Boolean hasKey = redisTemplate.hasKey(redisKey);
        if (hasKey != null && hasKey) {
            redisTemplate.delete(redisKey);
        } else {
            throw new TokenValidationException("토큰이 존재하지 않습니다.");
        }
    }
}
