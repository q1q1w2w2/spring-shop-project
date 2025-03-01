package com.example.demo1.service.facade;

import com.example.demo1.dto.user.*;
import com.example.demo1.entity.user.User;
import com.example.demo1.service.user.MailService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMailFacade {

    private final UserService userService;
    private final MailService mailService;

    @Transactional
    public UserResponseDto join(JoinDto joinDto) {
        User user = userService.join(joinDto);
        return new UserResponseDto(user);
    }

    public UserResponseDto getMyInfo() {
        return new UserResponseDto(getCurrentUser());
    }

    @Transactional
    public UserResponseDto updateUser(UpdateDto updateDto) {
        User updateUser = userService.update(getCurrentUser(), updateDto);
        return new UserResponseDto(updateUser);
    }

    public List<UserResponseDto> getUsers() {
        List<User> users = userService.findAllUser();
        return users.stream().
                map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean toggleUserBan(Long userIdx) {
        User user = userService.findById(userIdx);
        return userService.toggleUserBan(user);
    }

    public void sendMailForResetPassword(EmailRequestDto emailDto) {
        User user = userService.findByLoginId(emailDto.getLoginId());
        mailService.sendMailForResetPassword(user.getLoginId());
    }

    public void resetPassword(LoginDto loginDto) {
        userService.updatePassword(loginDto);
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }
}
