package com.example.demo1.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class JoinDto {

    @NotBlank(message = "사용자 이름은 비어있을 수 없습니다.")
    @Size(min = 2, max = 20, message = "사용자 이름의 길이는 2에서 20 사이어야 합니다.")
    private String username;

//    @NotBlank(message = "아이디는 비어있을 수 없습니다.")
//    @Size(min = 3, max = 50, message = "아이디의 길이는 3에서 50 사이어야 합니다.")
//    private String loginId;

    @Email
    @NotEmpty(message = "이메일은 비어있을 수 없습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
    @Size(min = 3, max = 100, message = "비밀번호의 길이는 3에서 100 사이어야 합니다.")
    private String password;

    @NotNull
    private LocalDate birth;

    @NotBlank(message = "전화번호는 비어있을 수 없습니다.")
    @Pattern(regexp = "^01[0-9]{1}-?[0-9]{3,4}-?[0-9]{4}$", message = "유효하지 않은 전화번호 형식입니다.")
    private String tel;

    @NotBlank(message = "주소는 비어있을 수 없습니다.")
    private String address;

    @NotBlank(message = "상세주소는 비어있을 수 없습니다.")
    private String detail;

//    private String provider;
}
