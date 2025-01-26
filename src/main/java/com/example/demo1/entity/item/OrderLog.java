package com.example.demo1.entity.item;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Slf4j
@Table(name = "order_log")
public class OrderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_idx")
    @Setter
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_idx")
    private Item item;

    @Column(name = "ea")
    private Integer ea;

    @Column(name = "review")
    private int review;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public OrderLog(Orders orders, Item item, int ea, int review) {
        this.orders = orders;
        this.item = item;
        this.ea = ea;
        this.review = review;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

    public static OrderLog createOrderLog(Orders orders, Item item, int ea) {
        OrderLog orderLog = OrderLog.builder()
                .orders(orders)
                .item(item)
                .ea(ea)
                .review(0)
                .build();

        orders.addOrderLog(orderLog);

        return orderLog;
    }

    public void updateReviewStatus(int reviewStatus) {
        this.review = reviewStatus;
    }
}
