package com.example.demo1.libs.auth;

import com.example.demo1.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class Auth {

    private final JwtProperties jwtProperties;
    private final Encrypt encrypt;

    public String generateToken(User user, Duration expiredAt){
        Date now = new Date();
        return makeToken(new Date(now.getTime()+expiredAt.toMillis()),user);
    }

    // 토큰 생성
    private String makeToken(Date expiry, User user){
        Date now = new Date();

        // 암호화를 위한 객체 생성
        TokenRequest request = TokenRequest.builder()
                .idx(user.getId())
                .id(user.getLoginId())
                .name(user.getUsername())
                .role(user.getAuthority())
                .build();

//        // 직렬화 ( 암호화를 위해 객체를 하나의 문자열로 만듬 )
        String serializedString = request.toString();

        // 양방향 암호화
        String cryptoString = encrypt.cryptoSync(serializedString);

        // return에서 서명 키가 HS256 알고리즘에 비해 작다는 에러 (256비트 -> 32바이트), 현재 80비트
        return Jwts.builder()
                // Header
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                // Payload
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("data", cryptoString)
                // Signature
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
//                .signWith(SignatureAlgorithm.HS256, generate16Bytes(jwtProperties.getSecretKey()))
                .compact();
    }

    // 랜덤 길이의 문자열을 16 바이트 해시로 생성한다.
//    private SecretKey generate16Bytes(String input){
//        try{
//            // SHA-256 알고리즘 사용
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hash = digest.digest(input.getBytes("UTF-8"));
//            return new SecretKeySpec(hash, 0, 32, "HmacSHA256");
//        } catch (Exception e){
//            throw new IllegalArgumentException("해시 생성 중 오류 발생", e);
//        }
//    }

//    // 토큰 검증
//    public boolean vaildToken(String token){
//        try{
//            Jwts.parser()
//                    .setSigningKey(jwtProperties.getSecretKey())    // 비밀키로 복호화
//                    .parseClaimsJws(token);
//            return true;
//        } catch(Exception e){
//            return false;
//        }
//    }

    // 토큰 기반으로 인증 정보를 가져옴 ( 토큰을 받아 클레임을 만들고 권한 정보를 빼 시큐리티 유저 객체를 만듬 )
    public Authentication getAuthentication(String token){

        TokenRequest tokenRequest = verifyToken(token);

        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(tokenRequest.getRole()));

        // Authentication 객체에 TokenRequest 전체를 저장
        return new UsernamePasswordAuthenticationToken(
                tokenRequest, // TokenRequest 객체 자체를 넣음
                token, // credentials (토큰)
                authorities // 권한 정보
        );
    }

    /*
    ⚠️ 인증 객체 호출 예제
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    TokenRequest tokenRequest = (TokenRequest) authentication.getPrincipal(); // 전체 정보가 들어있는 TokenRequest

     */

//    private Claims getClaims(String token){
//        return Jwts.parser()
//                .setSigningKey(jwtProperties.getSecretKey())
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    // 토큰 기반으로 유저 ID 를 가져오는 메서드
//    public Integer getUserIdx(String token){
//        Claims claims = getClaims(token);
//
//        return claims.get("idx", Integer.class);
//    }

    // 토큰을 검증하고 객체를 반환한다.
    public TokenRequest verifyToken(String token){
        try{
            // 토큰을 검증한다.
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
//                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // claim 의 암호화 된 문자열을 복호화 한다.
            String plainText = encrypt.decryptoSync(claims.get("data", String.class));

            // 역직렬화를 통해 반환한다.
            return TokenRequest.toObject(plainText);
        } catch(Exception e){
            throw new BadCredentialsException("유효하지 않은 토큰", e);
        }
    }
}
