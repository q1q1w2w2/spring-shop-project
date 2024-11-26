package com.example.demo1.dto.user;

import com.example.demo1.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDto {

    private Long idx;

    private String username;
    private String loginId;
    private String authority;
    private LocalDate birth;
    private String tel;
    private String address;
    private String detail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int ban;

    public UserResponseDto(User user) {
        this.idx = user.getId();
        this.username = user.getUsername();
        this.loginId = user.getLoginId();
        this.authority = user.getAuthority();
        this.birth = user.getBirth();
        this.tel = user.getTel();
        this.address = user.getAddress();
        this.detail = user.getDetail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.ban = user.getBan();
    }
}
