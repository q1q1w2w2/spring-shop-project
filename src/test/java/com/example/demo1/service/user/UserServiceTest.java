package com.example.demo1.service.user;

import com.example.demo1.domain.user.User;
import com.example.demo1.dto.user.JoinDto;
import com.example.demo1.dto.user.UpdateDto;
import com.example.demo1.exception.user.UserAlreadyExistException;
import com.example.demo1.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("회원가입 테스트")
    class joinTest {

        @Test
        @DisplayName("회원가입 성공")
        void join_success() {
            // given
            User user = createUser("kim", "testid", "010-0000-0001", "testpassword");

            // when
            User findUser = userService.findByLoginId(user.getLoginId());

            // then
            assertThat(findUser).isEqualTo(user);
            assertThat(findUser.getId()).isEqualTo(user.getId());
            assertThat(userService.findAll().size()).isEqualTo(1);
        }

        @Test
        @DisplayName("회원가입 실패: 중복된 아이디")
        void join_duplicatedLoginId() {
            // given
            createUser("kim", "testid", "testpassword", "010-0000-0001");

            // when & then
            assertThatThrownBy(() -> createUser("kim", "testid", "testpassword", "010-0000-0002"))
                    .isInstanceOf(UserAlreadyExistException.class)
                    .hasMessage("이미 존재하는 아이디입니다.");
        }

        @Test
        @DisplayName("회원가입 실패: 중복된 전화번호")
        void join_duplicatedTel() {
            // given
            createUser("kim", "testid", "testpassword", "010-0000-0001");

            // when & then
            assertThatThrownBy(() -> createUser("kim", "testid1", "testpassword", "010-0000-0001"))
                    .isInstanceOf(UserAlreadyExistException.class)
                    .hasMessage("이미 존재하는 전화번호입니다.");
        }
    }


    @Nested
    @DisplayName("회원 정보 테스트")
    class userTest {

        private User udpateUser(String updatePassword, String updateTel, String updateAddress, String updateDetail, User user) {
            UpdateDto updateDto = new UpdateDto(updatePassword, updateTel, updateAddress, updateDetail);
            return userService.update(user, updateDto);
        }

        @Test
        @DisplayName("회원 정보 수정 성공")
        void user_update_success1() {
            // given
            User user = createUser("kim", "testid", "testpassword", "010-0000-0001");

            // when
            String updatePassword = "updatePassword";
            String updateTel = "010-0000-0002";
            String updateAddress = "인천";
            String updateDetail = "3층";
            User updateUser = udpateUser(updatePassword, updateTel, updateAddress, updateDetail, user);

            // then
            assertThat(updateUser.getId()).isEqualTo(user.getId());

            // 기존 정보
            assertThat(updateUser.getLoginId()).isEqualTo(user.getLoginId());

            // 갱신한 정보
            assertThat(passwordEncoder.matches(updatePassword, updateUser.getPassword())).isTrue();
            assertThat(updateUser.getTel()).isEqualTo(updateTel);
            assertThat(updateUser.getAddress()).isEqualTo(updateAddress);
            assertThat(updateUser.getDetail()).isEqualTo(updateDetail);
        }

        @Test
        @DisplayName("회원 정보 수정 성공: 비밀번호가 null인 경우")
        void user_update_success2() {
            // given
            String password = "testpassword";
            User user = createUser("kim", "testid", password, "010-0000-0001");

            // when
            String updatePassword = null;
            String updateTel = "010-0000-0002";
            String updateAddress = "인천";
            String updateDetail = "3층";
            User updateUser = udpateUser(updatePassword, updateTel, updateAddress, updateDetail, user);

            // then
            assertThat(updateUser.getId()).isEqualTo(user.getId());

            // 기존 정보
            assertThat(updateUser.getLoginId()).isEqualTo(user.getLoginId());

            // 갱신한 정보
            assertThat(passwordEncoder.matches(password, updateUser.getPassword())).isTrue();
            assertThat(updateUser.getTel()).isEqualTo(updateTel);
            assertThat(updateUser.getAddress()).isEqualTo(updateAddress);
            assertThat(updateUser.getDetail()).isEqualTo(updateDetail);
        }

        @Test
        @DisplayName("회원 정보 수정 실패: 짧은 비밀번호")
        void user_update_fail() {
            // given
            User user = createUser("kim", "testid", "testpassword", "010-0000-0001");

            // when & then
            String updatePassword = "ab"; // 비밀번호 3~100자
            assertThatThrownBy(() -> udpateUser(updatePassword, "010-0000-0002", "인천", "3층", user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비밀번호의 길이는 3~100자 사이여야 합니다.");
        }
    }


    private User createUser(String name, String loginId, String password, String tel) {
        JoinDto dto = new JoinDto(name, loginId, password, LocalDate.now(), tel, "서울시", "내집");
        return userService.join(dto);
    }

}