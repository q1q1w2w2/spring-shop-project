package com.example.demo1.domain.item;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
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
    private Integer score; // 1~5 점

    @Column(name = "review")
    private String review;

    @Column(name = "blind")
    private int blind;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Review(OrderLog orderLog, String review, int score, int blind, LocalDateTime createAt, LocalDateTime updateAt) {
        this.orderLog = orderLog;
        this.review = review;
        this.score = score;
        this.blind = blind;
        this.createdAt = createAt;
        this.updatedAt = updateAt;
    }

    public void blind(int blind) {
        this.blind = blind;
    }
}
