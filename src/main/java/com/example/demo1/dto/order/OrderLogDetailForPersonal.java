package com.example.demo1.dto.order;

import com.example.demo1.entity.item.OrderLog;
import lombok.Data;

@Data
public class OrderLogDetailForPersonal {
    private Long itemIdx;
    private String itemName;
    private int itemPrice;
    private Long orderLogIdx;
    private int ea;
    private int review;

    public OrderLogDetailForPersonal(OrderLog orderLog) {
        this.itemIdx = orderLog.getItem().getIdx();
        this.itemName = orderLog.getItem().getItemName();
        this.itemPrice = orderLog.getItem().getPrice();
        this.orderLogIdx = orderLog.getIdx();
        this.ea = orderLog.getEa();
        this.review = orderLog.getReview();
    }
}
