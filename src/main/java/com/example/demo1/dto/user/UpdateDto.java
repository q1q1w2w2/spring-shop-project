package com.example.demo1.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateDto {

//    @Size(min = 3, max = 100, message = "비밀번호의 길이는 3에서 100 사이어야 합니다.")
    private String password;

    @NotBlank(message = "전화번호는 비어있을 수 없습니다.")
    @Pattern(regexp = "^01[0-9]{1}-?[0-9]{3,4}-?[0-9]{4}$", message = "유효하지 않은 전화번호 형식입니다.")
    private String tel;

    @NotBlank(message = "주소는 비어있을 수 없습니다.")
    private String address;

    private String detail;
}
