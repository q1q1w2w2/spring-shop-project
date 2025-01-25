package com.example.demo1.dto.order;

import com.example.demo1.entity.item.Orders;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailForPersonal {
    private Long orderIdx;
    private int step;
    private LocalDateTime orderAt;
    private List<OrderLogDetailForPersonal> orderLogs;

    public OrderDetailForPersonal(Orders orders, List<OrderLogDetailForPersonal> orderLogList) {
        this.orderIdx = orders.getIdx();
        this.step = orders.getStep();
        this.orderAt = orders.getCreatedAt();
        this.orderLogs = orderLogList;
    }
}
