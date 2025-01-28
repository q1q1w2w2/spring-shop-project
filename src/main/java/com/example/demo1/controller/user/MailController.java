package com.example.demo1.controller.user;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.dto.user.EmailCodeValidDto;
import com.example.demo1.dto.user.EmailRequestDto;
import com.example.demo1.service.user.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MailController {

    private final MailService mailService;

    @PostMapping("/api/mailSend")
    public ResponseEntity<ApiResponse<Object>> mailSend(@RequestBody @Valid EmailRequestDto emailDto) {
        mailService.joinEmail(emailDto.getLoginId());
        return ResponseEntity.ok(ApiResponse.success(OK, "인증 코드를 발송했습니다."));
    }

    @PostMapping("/api/mailCode/valid")
    public ResponseEntity<ApiResponse<Object>> mailCodeValid(@RequestBody @Valid EmailCodeValidDto dto) {
        mailService.checkAuthCode(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok(ApiResponse.success(OK, "인증 코드가 일치합니다."));
    }
}
