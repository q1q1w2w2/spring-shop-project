package com.example.demo1.entity.item;

import com.example.demo1.entity.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ItemCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_idx")
    private Item item;

    @Column(name = "ea")
    private int ea;

    @Column(name = "status")
    private int status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public ItemCart(User user, Item item, int ea) {
        this.user = user;
        this.item = item;
        this.ea = ea;
        this.status = 0;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

    public void updateEa(int ea) {
        this.ea = ea;
    }

    public void updateStatus(int status) {
        this.status = status;
    }
}
