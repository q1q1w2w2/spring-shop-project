package com.example.demo1.controller.user;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.dto.user.*;
import com.example.demo1.service.facade.UserMailFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserMailFacade userMailFacade;

    @PostMapping("/api/join")
    public ResponseEntity<ApiResponse<UserResponseDto>> join(@Validated @RequestBody JoinDto joinDto) {
        UserResponseDto user = userMailFacade.join(joinDto);
        return createResponse(CREATED, "회원가입이 완료되었습니다.", user);
    }

    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMyInfo() {
        UserResponseDto myInfo = userMailFacade.getMyInfo();
        return createResponse(OK, myInfo);
    }

    @PatchMapping("/api/update")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(@Validated @RequestBody UpdateDto updateDto) {
        UserResponseDto updateUser = userMailFacade.updateUser(updateDto);
        return createResponse(CREATED, "회원 정보가 수정되었습니다.", updateUser);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> users() {
        List<UserResponseDto> users = userMailFacade.getUsers();
        return createResponse(OK, users);
    }

    @PatchMapping("/user/ban/{userIdx}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> toggleUserBan(@PathVariable Long userIdx) {
        boolean ban = userMailFacade.toggleUserBan(userIdx);
        if (ban) {
            return createResponse(OK, "해당 사용자를 차단했습니다.");
        }
        return createResponse(OK, "해당 사용자의 차단을 해제했습니다.");
    }

    @PostMapping("/api/user/sendMail")
    public ResponseEntity<ApiResponse<Object>> sendMailForPasswordReset(@RequestBody EmailRequestDto emailDto) {
        userMailFacade.sendMailForResetPassword(emailDto);
        return createResponse(OK, "인증 코드를 발송했습니다.");
    }

    @PatchMapping("/api/pwd-reset")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@RequestBody @Validated LoginDto loginDto) {
        userMailFacade.resetPassword(loginDto);
        return createResponse(OK, "비밀번호가 재설정되었습니다.");
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, String message, T data) {
        ApiResponse<T> response = ApiResponse.success(status, message, data);
        return ResponseEntity.status(status).body(response);
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, String message) {
        ApiResponse<T> response = ApiResponse.success(status, message);
        return ResponseEntity.status(status).body(response);
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, T data) {
        ApiResponse<T> response = ApiResponse.success(status, data);
        return ResponseEntity.status(status).body(response);
    }
}
