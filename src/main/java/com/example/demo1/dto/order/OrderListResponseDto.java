package com.example.demo1.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderListResponseDto {
    private OrderResponseDto orderInfo;
    private List<OrderLogDetailDto> orderLogs;
}
