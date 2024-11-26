package com.example.demo1.domain.login;

import com.example.demo1.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @Column(name = "id")
    private String loginId;

    @Column(name = "name")
    private String username;

    @Column(name = "refreshtoken")
    private String refreshToken;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public RefreshToken(User user, String loginId, String username, String refreshToken, LocalDateTime createdAt, LocalDateTime expiredAt, LocalDateTime updatedAt) {
        this.user = user;
        this.loginId = loginId;
        this.username = username;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.updatedAt = updatedAt;
    }

    public void expiredRefreshToken() {
        this.expiredAt = LocalDateTime.now().withNano(0);
    }
}
