package com.example.demo1.service.user;

import com.example.demo1.exception.token.InvalidAuthCodeException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

@Service
@RequiredArgsConstructor
public class MailService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;
    private String authNumber;

    private static final String AUTH_CODE_PREFIX = "authCode:";
    private static final int AUTH_CODE_TTL = 5;

    public void makeRandomNumber() {
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            codeBuilder.append(random.nextInt(10));
        }
        authNumber = codeBuilder.toString();
    }

    public String joinEmail(String email) {
        return sendMail(email, "[demo2] 회원 가입 인증 메일입니다.");
    }

    public String sendMailForResetPassword(String email) {
        return sendMail(email, "[demo2] 비밀번호 재설정 인증 메일입니다.");
    }

    public String sendMail(String email, String title) {
        makeRandomNumber();
        String setFrom = "xmrrhdwjdqls@gmail.com";
        String toMail = email;
        String content =
                "<br>" +
                        "<h2>인증 번호는 " + authNumber + "입니다.</h2>" +
                        "<br>";

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(AUTH_CODE_PREFIX + email, authNumber, AUTH_CODE_TTL, MINUTES);

        mailSend(setFrom, toMail, title, content);

        return authNumber;
    }

    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("인증코드 발송에 실패했습니다: " + e.getMessage());
        }
    }

    public void checkAuthCode(String email, String authCode) {
        String storeAuthCode = redisTemplate.opsForValue().get(AUTH_CODE_PREFIX + email);
        if (!authCode.equals(storeAuthCode)) {
            throw new InvalidAuthCodeException();
        }
    }
}
