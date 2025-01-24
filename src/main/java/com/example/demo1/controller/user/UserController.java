package com.example.demo1.controller.user;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.entity.user.User;
import com.example.demo1.dto.user.*;
import com.example.demo1.service.user.MailSendService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final MailSendService mailService;

    @PostMapping("/api/join")
    public ResponseEntity<ApiResponse<UserResponseDto>> join(@Validated @RequestBody JoinDto dto) {
        User user = userService.join(dto);

        ApiResponse<UserResponseDto> response = ApiResponse.success(CREATED, "회원가입이 완료되었습니다.", new UserResponseDto(user));
        return ResponseEntity.status(CREATED).body(response);
    }

    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMyInfo() {
        User user = userService.getCurrentUser();

        ApiResponse<UserResponseDto> response = ApiResponse.success(OK, new UserResponseDto(user));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/api/update")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(@Validated @RequestBody UpdateDto dto) {
        User user = userService.getCurrentUser();
        User updateUser = userService.update(user, dto);

        ApiResponse<UserResponseDto> response = ApiResponse.success(CREATED, "회원 정보가 수정되었습니다.", new UserResponseDto(updateUser));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> users() {
        List<User> userList = userService.findAllUser();
        List<UserResponseDto> userDtoList = new ArrayList<>();
        for (User user : userList) {
            userDtoList.add(new UserResponseDto(user));
        }
        ApiResponse<List<UserResponseDto>> response = ApiResponse.success(OK, userDtoList);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/user/ban/{userIdx}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> banOrUnbanUser(@PathVariable Long userIdx) {
        User user = userService.findById(userIdx);
        boolean ban = userService.banOrUnban(user);
        if (ban) {
            return ResponseEntity.ok(ApiResponse.success(OK, "해당 유저를 차단했습니다."));
        }
        return ResponseEntity.ok(ApiResponse.success(OK, "해당 유저의 차단을 해제했습니다."));
    }

    /**
     * 비밀번호 재설정(이메일 인증)
     * 1. 이메일을 입력하여 api 호출 -> 이메일 검증 + mail 전송
     * 2. 인증번호로 인증이 완료되면(/api/mailCode/valid) 비밀번호 설정 페이지로 이동
     * 3. 해당 페이지에서 새로운 비밀번호 입력 후 정보 수정
     */
    @PostMapping("/api/user/sendMail")
    public ResponseEntity<ApiResponse<Object>> sendMailForPasswordReset(@RequestBody EmailRequestDto dto) {
        userService.findByLoginId(dto.getLoginId());
        mailService.sendMailForPasswordReset(dto.getLoginId());
        return ResponseEntity.ok(ApiResponse.success(OK, "인증 코드를 발송했습니다."));
    }

    @PatchMapping("/api/pwd-reset")
    public ResponseEntity<ApiResponse<Object>> passwordReset(@RequestBody @Validated LoginDto dto) {
        userService.updatePassword(dto);
        return ResponseEntity.ok(ApiResponse.success(OK, "비밀번호가 재설정되었습니다."));
    }
}
