package com.example.demo1.service.login;

import com.example.demo1.dto.auth.TokensDto;
import com.example.demo1.dto.user.RefreshTokenDto;
import com.example.demo1.exception.token.TokenValidationException;
import com.example.demo1.util.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;

    @Transactional
    public TokensDto refreshTokens(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new TokenValidationException("토큰이 유효하지 않습니다.");
        }
        String subject = tokenProvider.extractUserIdFromRefreshToken(refreshToken);

        String accessToken = tokenProvider.createNewAccessToken(refreshToken);
        String newRefreshToken = tokenProvider.createRefreshToken(subject);
        return new TokensDto(accessToken, newRefreshToken);
    }
}
