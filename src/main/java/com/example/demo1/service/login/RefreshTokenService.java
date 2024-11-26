package com.example.demo1.service.login;

import com.example.demo1.domain.login.RefreshToken;
import com.example.demo1.domain.user.User;
import com.example.demo1.exception.token.TokenValidationException;
import com.example.demo1.exception.user.UserNotFoundException;
import com.example.demo1.repository.login.RefreshTokenRepository;
import com.example.demo1.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveRefreshToken(String refreshToken, String userId) {

        User user = userRepository.findByLoginId(userId)
                .orElseThrow(UserNotFoundException::new);

        // 새로운 토큰 저장 전 만료시간이 찍히지 않은 토큰 모두 만료시키기
        List<RefreshToken> nonExpiredRefreshToken = refreshTokenRepository.findNonExpiredRefreshTokensByUser(user)
                .orElse(null);

        if (nonExpiredRefreshToken != null) {
            for (RefreshToken token : nonExpiredRefreshToken) {
                token.expiredRefreshToken();
            }
        }

        refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken(refreshToken)
                .user(user)
                .loginId(user.getLoginId())
                .username(user.getUsername())
                .createdAt(LocalDateTime.now().withNano(0))
                .updatedAt(LocalDateTime.now().withNano(0))
                .expiredAt(null)
                .build());
    }

    // refresh token 만료 메서드
    @Transactional
    public void expiredRefreshToken(String refreshToken) {
        log.info("만료시킬 refreshToken: {}", refreshToken);
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new TokenValidationException("토큰이 존재하지 않습니다."));

        if (token.getExpiredAt() != null) {
            throw new TokenValidationException("이미 만료된 토큰입니다.");
        }

        token.expiredRefreshToken();
    }

    // refresh token 의 expiredAt에 값이 있으면 true 반환(이미 만료되었으면)
    public boolean isRefreshTokenExpired(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new TokenValidationException("토큰이 존재하지 않습니다."));
        return token.getExpiredAt() != null;
    }

}
