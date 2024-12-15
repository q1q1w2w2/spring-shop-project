package com.example.demo1.service.login;

import com.example.demo1.domain.user.User;
import com.example.demo1.dto.user.JoinDto;
import com.example.demo1.dto.user.LoginDto;
import com.example.demo1.exception.token.TokenValidationException;
import com.example.demo1.service.user.UserService;
import com.example.demo1.util.jwt.TokenProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired UserService userService;
    @Autowired AuthService authService;
    @Autowired TokenProvider tokenProvider;

    private final String LOGIN_ID = "testLoginId";
    private final String LOGIN_PASSWORD = "testPassword";

    @Nested
    @DisplayName("로그인 테스트")
    class loginTest {
        @Test
        @DisplayName("로그인 성공")
        void login_success() throws Exception {
            // given
            createUser("kim", LOGIN_ID, LOGIN_PASSWORD, "010-0000-0001");

            // when
            Map<String, String> tokens = authService.login(new LoginDto(LOGIN_ID, LOGIN_PASSWORD));
            String accessToken = tokens.get("accessToken");
            String refreshToken = tokens.get("refreshToken");

            // then
            assertThat(accessToken).isNotNull();
            assertThat(refreshToken).isNotNull();

            // security context 확인
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNotNull();
            assertThat(authentication.isAuthenticated()).isTrue();
            assertThat(authentication.getName()).isEqualTo(LOGIN_ID);
        }

        @ParameterizedTest
        @MethodSource("provideInvalidCredentials")
        @DisplayName("로그인 실패: 잘못된 아이디 또는 비밀번호 입력")
        void login_fail_invalidCredentials(String loginId, String password) {
            // given
            createUser("kim", LOGIN_ID, LOGIN_PASSWORD, "010-0000-0001");

            // when & then
            assertThrows(BadCredentialsException.class,
                    () -> authService.login(new LoginDto(loginId, password))
            );
        }

        private static Stream<Arguments> provideInvalidCredentials() {
            return Stream.of(
                    Arguments.of("wrongLoginId", "testPassword"), // 잘못된 아이디
                    Arguments.of("testLoginId", "wrongPassword") // 잘못된 비밀번호
            );
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class logoutTest {

        private String accessToken;
        private String refreshToken;

        @BeforeEach
        void setUp() throws Exception {
            // 테스트를 위한 사용자 생성
            createUser("kim", LOGIN_ID, LOGIN_PASSWORD, "010-0000-0001");

            // 로그인 후 토큰 발급
            Map<String, String> tokens = authService.login(new LoginDto(LOGIN_ID, LOGIN_PASSWORD));
            accessToken = tokens.get("accessToken");
            refreshToken = tokens.get("refreshToken");
        }

        @Test
        @DisplayName("로그아웃 성공")
        void logout_success() throws Exception {
            // given & when
            authService.logout(accessToken, refreshToken);
        }

        @Test
        @DisplayName("로그아웃 실패: 이미 로그아웃 된 사용자")
        void logout_fail_alreadyLogout() throws Exception {
            // given
            authService.logout(accessToken, refreshToken); // 첫 번째 로그아웃

            // when & then
            assertThrows(TokenValidationException.class,
                    () -> authService.logout(accessToken, refreshToken) // 두 번째 로그아웃
            );
        }
    }


    private User createUser(String name, String loginId, String password, String tel) {
        JoinDto dto = new JoinDto(name, loginId, password, LocalDate.now(), tel, "서울시", "내집");
        return userService.join(dto);
    }
}