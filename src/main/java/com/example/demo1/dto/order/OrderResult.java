package com.example.demo1.dto.order;

import com.example.demo1.entity.item.OrderLog;
import com.example.demo1.entity.item.Orders;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderResult {

    private Orders orders;
    private List<OrderLog> orderLogs;

    public OrderResult(Orders orders, List<OrderLog> orderLogs) {
        this.orders = orders;
        this.orderLogs = orderLogs;
    }
}
