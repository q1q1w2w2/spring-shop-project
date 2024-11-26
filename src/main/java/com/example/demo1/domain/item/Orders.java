package com.example.demo1.domain.item;

import com.example.demo1.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "step")
    private int step; // 주문완료:1, 배송시작:2, 배송완료:3, 취소:10

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<OrderLog> orderLogs = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Orders(User user, LocalDateTime date, int step, List<OrderLog> orderLogs, LocalDateTime createAt, LocalDateTime updateAt) {
        this.user = user;
        this.date = date;
        this.step = step;
        this.orderLogs = orderLogs;
        this.createdAt = createAt;
        this.updatedAt = updateAt;
    }

    public static Orders createOrders(User user) {
        Orders orders = Orders.builder()
                .user(user)
                .date(LocalDateTime.now())
                .step(1) // 초기 주문 상태
                .orderLogs(new ArrayList<>())
                .createAt(LocalDateTime.now().withNano(0))
                .updateAt(LocalDateTime.now().withNano(0))
                .build();

        return orders;
    }

    public void addOrderLog(OrderLog orderLog) {
        orderLogs.add(orderLog);
        orderLog.setOrders(this);
    }

    public void updateStep(int step) {
        this.step = step;
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

}
