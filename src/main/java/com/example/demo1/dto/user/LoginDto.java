package com.example.demo1.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class LoginDto {

    @NotBlank(message = "아이디는 공백일 수 없습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    private String password;
}
