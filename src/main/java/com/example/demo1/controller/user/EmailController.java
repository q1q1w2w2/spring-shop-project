package com.example.demo1.controller.user;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.dto.user.EmailCodeValidDto;
import com.example.demo1.dto.user.EmailRequestDto;
import com.example.demo1.service.user.MailSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final MailSendService mailService;

    @PostMapping("/api/mailSend")
    public ResponseEntity mailSend(@RequestBody @Valid EmailRequestDto emailDto) {
        mailService.joinEmail(emailDto.getLoginId());
        return ResponseEntity.ok(ApiResponse.success(OK, "인증 코드를 발송했습니다."));
    }

    @PostMapping("/api/mailCode/valid")
    public ResponseEntity mailCodeValid(@RequestBody @Valid EmailCodeValidDto dto) {
        boolean isValid = mailService.checkAuthCode(dto.getEmail(), dto.getCode());
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success(OK, "인증 코드가 일치합니다."));
        }
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ApiResponse.error(UNAUTHORIZED, "인증 코드가 일치하지 않습니다."));
    }
}
