package com.example.demo1.web.user;

import com.example.demo1.domain.user.User;
import com.example.demo1.dto.user.*;
import com.example.demo1.service.user.MailSendService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final MailSendService mailService;

    // 회원가입
    @PostMapping("/api/join")
    public ResponseEntity<Map<String, Object>> join(@Validated @RequestBody JoinDto dto) {
        User user = userService.join(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "회원가입이 완료되었습니다!");
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

    // 본인 정보
    @GetMapping("/mypage")
    public ResponseEntity<UserResponseDto> getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getCurrentUser(authentication);
        UserResponseDto response = new UserResponseDto(user);
        return ResponseEntity.ok(response);
    }

    // 회원 정보 업데이트(본인 정보)
    @PatchMapping("/api/update")
    public ResponseEntity<Map<String, Object>> update(@Validated @RequestBody UpdateDto dto) {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
        User updateUser = userService.update(user, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "회원 업데이트 완료");
        response.put("user", updateUser);

        return ResponseEntity.ok(response);
    }

    // 전체 유저 정보 출력(admin 제외)
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity users() {
        List<User> userList = userService.findAllUser();
        List<UserResponseDto> response = new ArrayList<>();
        for (User user : userList) {
            response.add(new UserResponseDto(user));
        }
        return ResponseEntity.ok(response);
    }

    // 유저 차단(ban)
    @PatchMapping("/user/ban/{userIdx}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity banOrUnbanUser(@PathVariable Long userIdx) {
        User user = userService.findById(userIdx);
        boolean ban = userService.banOrUnban(user);
        if (ban) {
            return ResponseEntity.ok(Map.of("message", "해당 유저를 차단했습니다."));
        }
        return ResponseEntity.ok(Map.of("message", "해당 유저의 차단을 해제했습니다."));
    }

    /**
     * 비밀번호 재설정(이메일 인증)
     * 1. 이메일을 입력하여 api 호출 -> 이메일 검증 + mail 전송
     * 2. 인증번호로 인증이 완료되면(/api/mailCode/valid) 비밀번호 설정 페이지로 이동
     * 3. 해당 페이지에서 새로운 비밀번호 입력 후 정보 수정
     */
    @PostMapping("/api/user/sendMail")
    public ResponseEntity sendMailForPasswordReset(@RequestBody EmailRequestDto dto) {
        userService.findByLoginId(dto.getLoginId()); // 없으면 예외발생
        mailService.sendMailForPasswordReset(dto.getLoginId());
        return ResponseEntity.ok(Map.of("message", "인증 코드를 발송했습니다."));
    }

    @PatchMapping("/api/pwd-reset")
    public ResponseEntity passwordReset(@RequestBody @Validated LoginDto dto) {
        userService.updatePassword(dto);
        return ResponseEntity.ok(Map.of("message", "비밀번호가 재설정되었습니다."));
    }
}
