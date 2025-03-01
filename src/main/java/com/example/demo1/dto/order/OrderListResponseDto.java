package com.example.demo1.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponseDto {
    private OrderResponseDto orderInfo;
    private List<OrderLogDetailDto> orderLogs;
}
