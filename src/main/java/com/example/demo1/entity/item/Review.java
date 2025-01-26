package com.example.demo1.entity.item;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item_review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_idx")
    private OrderLog orderLog;

    @Column(name = "score")
    private int score;

    @Column(name = "review")
    private String review;

    @Column(name = "blind")
    private int blind;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Review(OrderLog orderLog, String review, int score, int blind) {
        this.orderLog = orderLog;
        this.review = review;
        this.score = score;
        this.blind = blind;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

    public void blind(int blind) {
        this.blind = blind;
    }
}
