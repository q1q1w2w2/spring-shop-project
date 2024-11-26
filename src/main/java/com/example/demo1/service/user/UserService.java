package com.example.demo1.service.user;

import com.example.demo1.domain.user.User;
import com.example.demo1.dto.user.JoinDto;
import com.example.demo1.dto.user.LoginDto;
import com.example.demo1.dto.user.UpdateDto;
import com.example.demo1.exception.user.UserAlreadyExistException;
import com.example.demo1.exception.user.UserNotFoundException;
import com.example.demo1.libs.auth.TokenRequest;
import com.example.demo1.repository.user.UserRepository;
import com.example.demo1.util.constant.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.demo1.util.constant.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입(일반 사용자)
    @Transactional
    public User join(JoinDto dto) {

        if (userRepository.findByLoginId(dto.getLoginId()).isPresent()) {
            throw new UserAlreadyExistException("이미 존재하는 아이디입니다.");
        }
        if (userRepository.findByTel(dto.getTel()).isPresent()) {
            throw new UserAlreadyExistException("이미 존재하는 전화번호입니다.");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .birth(dto.getBirth())
                .tel(dto.getTel())
                .address(dto.getAddress())
                .detail(dto.getDetail())
                .loginId(dto.getLoginId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .authority("ROLE_USER")
                .createdAt(LocalDateTime.now().withNano(0))
                .updatedAt(LocalDateTime.now().withNano(0))
                .build();

        return userRepository.save(user);
    }

    // 회원가입(Admin)
    @Transactional
    public User joinAdmin(JoinDto dto) {

        if (userRepository.findByLoginId(dto.getLoginId()).isPresent()) {
            throw new UserAlreadyExistException("이미 존재하는 아이디입니다.");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .birth(dto.getBirth())
                .tel(dto.getTel())
                .address(dto.getAddress())
                .detail(dto.getDetail())
                .loginId(dto.getLoginId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .authority("ROLE_ADMIN")
                .createdAt(LocalDateTime.now().withNano(0))
                .updatedAt(LocalDateTime.now().withNano(0))
                .build();

        return userRepository.save(user);
    }

    // 회원가입(OAuth)
    @Transactional
    public User joinOAuth(JoinDto dto) {

        if (userRepository.findByLoginId(dto.getLoginId()).isPresent()) {
            throw new UserAlreadyExistException("이미 존재하는 아이디입니다.");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .birth(dto.getBirth())
                .tel(dto.getTel())
                .address(dto.getAddress())
                .detail(dto.getDetail())
                .loginId(dto.getLoginId())
                .authority("ROLE_USER")
                .createdAt(LocalDateTime.now().withNano(0))
                .updatedAt(LocalDateTime.now().withNano(0))
//                .provider(dto.getProvider())
                .build();

        return userRepository.save(user);
    }

    // 사용자 정보 수정
    @Transactional
    public User update(User user, UpdateDto dto) {
        User.UserBuilder userBuilder = User.builder()
                .updatedAt(LocalDateTime.now().withNano(0));
        updateInfo(user, dto, userBuilder);

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            if (dto.getPassword().length() < 3 || dto.getPassword().length() > 100) {
                throw new IllegalArgumentException("비밀번호의 길이는 3~100자 사이여야 합니다.");
            }
            userBuilder.password(passwordEncoder.encode(dto.getPassword()));
            return user.updateWithPassword(userBuilder.build());
        }
        return user.updateWithoutPassword(userBuilder.build());
    }

    private void updateInfo(User user, UpdateDto dto, User.UserBuilder userBuilder) {
        userBuilder.address(dto.getAddress() != null ? dto.getAddress() : user.getAddress());
        userBuilder.detail(dto.getDetail() != null ? dto.getDetail() : user.getDetail());
        userBuilder.tel(dto.getTel() != null ? dto.getTel() : user.getTel());
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(UserNotFoundException::new);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllUser() {
        return userRepository.findAllByAuthority("ROLE_USER");
    }

    // 현재 사용자
    public User getCurrentUser(Authentication authentication) {
        TokenRequest tokenRequest = TokenRequest.toObject(authentication.getPrincipal().toString());
        return findByLoginId(tokenRequest.getId());
    }

    // 사용자 밴/해제
    @Transactional
    public boolean banOrUnban(User user) {
        // true: ban / false: unban
        if (user.getBan() == USER_UNBAN) {
            user.banOrUnban(USER_BAN);
            return true;
        } else {
            user.banOrUnban(USER_UNBAN);
            return false;
        }
    }

    // 비밀번호 재설정
    @Transactional
    public void updatePassword(LoginDto dto) {
        User user = findByLoginId(dto.getLoginId());
        user.updatePassword(passwordEncoder.encode(dto.getPassword()));
    }
}