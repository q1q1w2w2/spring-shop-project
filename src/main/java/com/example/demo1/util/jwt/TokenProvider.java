package com.example.demo1.util.jwt;

import com.example.demo1.service.login.CustomUserDetailsService;
import com.example.demo1.util.aes.AesUtil;
import com.example.demo1.util.constant.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

import static com.example.demo1.util.constant.Role.*;

@Component
@Slf4j
public class TokenProvider implements InitializingBean {

    private static final String CLAIM_AUTHORITY = "authority";
    private static final String REDIS_REFRESH_KEY_PREFIX = "refreshToken:";

    private SecretKey key;
    private SecretKey claimKey;

    private final String secret;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expire-time}") long accessTokenExpireTime,
            @Value("${jwt.refresh-token-expire-time}") long refreshTokenExpireTime,
            @Value("${jwt.claim-key}") String claimKeyString,
            CustomUserDetailsService customUserDetailsService,
            RedisTemplate<String, String> redisTemplate
    ) throws Exception {
        this.secret = secret;
        this.accessTokenExpireTime = accessTokenExpireTime * 1000;
        this.refreshTokenExpireTime = refreshTokenExpireTime * 1000;
        this.claimKey = AesUtil.generateKeyFromString(claimKeyString);
        this.customUserDetailsService = customUserDetailsService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] decode = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(decode);
    }

    public String createAccessToken(String subject, String authority) throws Exception {
        String encryptSubject = AesUtil.encrypt(subject, claimKey);
        String encryptAuthority = AesUtil.encrypt(authority, claimKey);

        return createToken(encryptSubject, encryptAuthority, accessTokenExpireTime);
    }

    public String createRefreshToken(String subject) throws Exception {
        String encryptSubject = AesUtil.encrypt(subject, claimKey);
        String encryptAuthority = AesUtil.encrypt(ROLE_USER.toString(), claimKey);
        String refreshToken = createToken(encryptSubject, encryptAuthority, refreshTokenExpireTime);

        redisTemplate.opsForValue().set(REDIS_REFRESH_KEY_PREFIX + subject, refreshToken);

        return refreshToken;
    }

    public String createNewAccessToken(String refreshToken) throws Exception {
        String authority = getAuthorityFromRefreshToken(refreshToken);
        String subject = extractUserIdFromRefreshToken(refreshToken);
        return createAccessToken(subject, authority);
    }

    private String createToken(String subject, String authority, long expireTime) {
        long now = new Date().getTime();
        Date validity = new Date(now + expireTime);

        return Jwts.builder()
                .subject(subject)
                .claim(CLAIM_AUTHORITY, authority)
                .issuedAt(new Date())
                .expiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Authentication getAuthentication(String token) throws Exception {
        Claims claims = Jwts.parser()
                .verifyWith(key) // secretKey
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String subject = AesUtil.decrypt(claims.getSubject(), claimKey);
        String encryptAuthority = Optional.ofNullable(claims.get(CLAIM_AUTHORITY))
                .map(Object::toString)
                .orElse(ROLE_USER.toString());
        String authority = AesUtil.decrypt(encryptAuthority, claimKey);

        return createAuthentication(subject, token, authority);
    }

    public String getAuthorityFromRefreshToken(String refreshToken) throws Exception {
        String subject = extractUserIdFromRefreshToken(refreshToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(subject);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(ROLE_USER.toString());
    }

    private Authentication createAuthentication(String subject, String token, String authority) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(authority));

        User principal = new User(subject, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public String extractUserIdFromRefreshToken(String refreshToken) throws Exception {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getPayload();
        String encryptSubject = claims.getSubject();
        return AesUtil.decrypt(encryptSubject, claimKey);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            log.info("토큰 검증 성공");
            return true;
        } catch (SignatureException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 서명입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

}
