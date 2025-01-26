package com.example.demo1.entity.item;

import com.example.demo1.entity.user.User;
import com.example.demo1.util.constant.OrderStep;
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

//    @Enumerated(EnumType.ORDINAL)
//    private OrderStep step;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<OrderLog> orderLogs = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Orders(User user, LocalDateTime date, OrderStep step, List<OrderLog> orderLogs) {
        this.user = user;
        this.date = date;
        this.step = step.getValue();
        this.orderLogs = orderLogs;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

    public static Orders createOrders(User user) {
        Orders orders = Orders.builder()
                .user(user)
                .date(LocalDateTime.now())
                .step(OrderStep.ORDER_COMP) // 초기 주문 상태
                .orderLogs(new ArrayList<>())
                .build();

        return orders;
    }

    public void addOrderLog(OrderLog orderLog) {
        orderLogs.add(orderLog);
        orderLog.setOrders(this);
    }

    public void updateStep(OrderStep step) {
        this.step = step.getValue();
    }

}
