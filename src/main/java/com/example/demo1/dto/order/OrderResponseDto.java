package com.example.demo1.dto.order;

import com.example.demo1.entity.item.Orders;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderResponseDto {
    private Long idx;
    private Long userIdx;
    private int step;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String username;
    private String loginId;
    private String tel;
    private String address;
    private String detail;

    public OrderResponseDto(Orders orders) {
        this.idx = orders.getIdx();
        this.userIdx = orders.getUser().getId();
        this.step = orders.getStep();
        this.createdAt = orders.getCreatedAt();
        this.updatedAt = orders.getUpdatedAt();

        this.username = orders.getUser().getUsername();
        this.loginId = orders.getUser().getLoginId();
        this.tel = orders.getUser().getTel();
        this.address = orders.getUser().getAddress();
        this.detail = orders.getUser().getDetail();
    }

}
