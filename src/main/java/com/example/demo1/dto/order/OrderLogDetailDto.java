package com.example.demo1.dto.order;

import com.example.demo1.entity.item.OrderLog;
import lombok.Data;

@Data
public class OrderLogDetailDto {
    private Long itemIdx;
    private String itemName;
    private int ea;

    public OrderLogDetailDto(OrderLog orderLog) {
        this.itemIdx = orderLog.getIdx();
        this.itemName = orderLog.getItem().getItemName();
        this.ea = orderLog.getEa();
    }
}
