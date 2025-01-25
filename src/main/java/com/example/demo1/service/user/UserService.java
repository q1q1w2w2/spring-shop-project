package com.example.demo1.service.user;

import com.example.demo1.entity.user.User;
import com.example.demo1.dto.user.JoinDto;
import com.example.demo1.dto.user.LoginDto;
import com.example.demo1.dto.user.UpdateDto;
import com.example.demo1.exception.user.UserAlreadyExistException;
import com.example.demo1.exception.user.UserNotFoundException;
import com.example.demo1.repository.user.UserRepository;
import com.example.demo1.util.constant.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.demo1.util.constant.Constants.*;
import static com.example.demo1.util.constant.Role.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User join(JoinDto dto) {
        return joinUser(dto, ROLE_USER);
    }

    @Transactional
    public User joinAdmin(JoinDto dto) {
        return joinUser(dto, ROLE_ADMIN);
    }

    @Transactional
    public User joinOAuth(JoinDto dto) {
        checkUserExistence(dto);

        User user = User.builder()
                .username(dto.getUsername())
                .birth(dto.getBirth())
                .tel(dto.getTel())
                .address(dto.getAddress())
                .detail(dto.getDetail())
                .loginId(dto.getLoginId())
                .authority(ROLE_USER.toString())
                .createdAt(LocalDateTime.now().withNano(0))
                .updatedAt(LocalDateTime.now().withNano(0))
                .build();

        return userRepository.save(user);
    }

    private User joinUser(JoinDto dto, Role role) {
        checkUserExistence(dto);

        User user = User.builder()
                .username(dto.getUsername())
                .birth(dto.getBirth())
                .tel(dto.getTel())
                .address(dto.getAddress())
                .detail(dto.getDetail())
                .loginId(dto.getLoginId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .authority(role.toString())
                .createdAt(LocalDateTime.now().withNano(0))
                .updatedAt(LocalDateTime.now().withNano(0))
                .build();

        return userRepository.save(user);
    }

    private void checkUserExistence(JoinDto dto) {
        if (userRepository.findByLoginId(dto.getLoginId()).isPresent()) {
            throw new UserAlreadyExistException("이미 존재하는 아이디입니다.");
        }
        if (userRepository.findByTel(dto.getTel()).isPresent()) {
            throw new UserAlreadyExistException("이미 존재하는 전화번호입니다.");
        }
    }

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
        return userRepository.findAllByAuthority(ROLE_USER.toString());
    }

    public User getCurrentUser(Authentication authentication) {
        return findByLoginId(authentication.getName());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return findByLoginId(authentication.getName());
    }

    @Transactional
    public boolean banOrUnban(User user) {
        if (user.getBan() == USER_UNBAN) {
            user.banOrUnban(USER_BAN);
            return true;
        } else {
            user.banOrUnban(USER_UNBAN);
            return false;
        }
    }

    @Transactional
    public void updatePassword(LoginDto dto) {
        User user = findByLoginId(dto.getLoginId());
        user.updatePassword(passwordEncoder.encode(dto.getPassword()));
    }
}