package com.example.demo1.entity.user;

import com.example.demo1.entity.item.ItemCart;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long id;

    @Column(name = "name", length = 50)
    private String username;

    @Column(name = "id", length = 50, unique = true)
    private String loginId;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "role")
    private String authority;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "tel")
    private String tel;

    @Column(name = "address")
    private String address;

    @Column(name = "detail")
    private String detail;

    @Column(name = "ban")
    private int ban;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ItemCart> itemCarts = new ArrayList<>();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public User(Long id, String username, String loginId, String password, String authority, LocalDate birth, String tel, String address, String detail, LocalDateTime createdAt, LocalDateTime updatedAt, int ban) {
        this.id = id;
        this.username = username;
        this.loginId = loginId;
        this.password = password;
        this.authority = authority;
        this.birth = birth;
        this.tel = tel;
        this.address = address;
        this.detail = detail;
        this.ban = ban;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
//        this.provider = provider;
    }

    // 사용자 정보 수정
    public User updateWithPassword(User user) {
        this.password = user.getPassword();
        this.tel = user.getTel();
        this.address = user.getAddress();
        this.detail = user.getDetail();
        return this;
    }

    // 비밀번호 제외하고 수정
    public User updateWithoutPassword(User user) {
        this.tel = user.getTel();
        this.address = user.getAddress();
        this.detail = user.getDetail();
        return this;
    }

    // 비밀번호만 수정
    public User updatePassword(String password) {
        this.password = password;
        return this;
    }

    public User updateUsername(String username) {
        this.username = username;
        return this;
    }

    public void banOrUnban(int ban) {
        this.ban = ban;
    }
}
