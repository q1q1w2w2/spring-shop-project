package com.example.demo1.controller.item;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.entity.item.OrderLog;
import com.example.demo1.entity.item.Orders;
import com.example.demo1.entity.user.User;
import com.example.demo1.dto.order.*;
import com.example.demo1.service.item.OrderLogService;
import com.example.demo1.service.item.OrderService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final OrderLogService orderLogService;

    @PostMapping("/api/order")
    public ResponseEntity<ApiResponse<Object>> createOrders(@Validated @RequestBody List<CreateOrdersDto> dtoList) {
        User user = userService.getCurrentUser();
        orderService.saveOrders(user, dtoList);

        ApiResponse<Object> response = ApiResponse.success(OK, "주문이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/list/admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderListResponseDto>> orderListAdmin(OrderSearch orderSearch) {
        OrderListResponseDto orderListResponseDto = new OrderListResponseDto();

        List<Orders> orders = orderService.findAll(orderSearch);
        for (Orders order : orders) {
            orderListResponseDto.setOrderInfo(new OrderResponseDto(order));

            List<OrderLogDetailDto> orderLogResponse = new ArrayList<>();
            for (OrderLog orderLog : order.getOrderLogs()) {
                orderLogResponse.add(new OrderLogDetailDto(orderLog));
            }
            orderListResponseDto.setOrderLogs(orderLogResponse);
        }

        ApiResponse<OrderListResponseDto> response = ApiResponse.success(OK, orderListResponseDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/list")
    public ResponseEntity<ApiResponse<Object>> orderList(OrderSearch orderSearch) {
        User user = userService.getCurrentUser();
        List<OrderDetailForPersonal> orders = orderService.findAll(orderSearch, user);

        ApiResponse<Object> response = ApiResponse.success(OK, orders);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/list/{orderIdx}")
    public ResponseEntity<ApiResponse<List<OrderLogResponseDto>>> orderDetail(@PathVariable Long orderIdx) {
        List<OrderLog> orderLogs = orderLogService.findByOrderIdx(orderIdx);
        List<OrderLogResponseDto> orderLogDtoList = new ArrayList<>();
        for (OrderLog orderLog : orderLogs) {
            orderLogDtoList.add(new OrderLogResponseDto(orderLog));
        }

        ApiResponse<List<OrderLogResponseDto>> response = ApiResponse.success(OK, orderLogDtoList);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/api/order/start")
    public ResponseEntity<ApiResponse<Object>> startOrder(@Validated @RequestBody OrderRequestDto dto) {
        orderService.startOrders(dto.getOrderIdx());

        ApiResponse<Object> response = ApiResponse.success(OK, "배송 시작 상태로 변경되었습니다.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/api/order/complete")
    public ResponseEntity<ApiResponse<Object>> completeOrder(@Validated @RequestBody OrderRequestDto dto) {
        orderService.completeOrders(dto.getOrderIdx());

        ApiResponse<Object> response = ApiResponse.success(OK, "배송 완료 상태로 변경되었습니다.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/api/order/cancel")
    public ResponseEntity<ApiResponse<Object>> cancelOrder(@Validated @RequestBody OrderRequestDto dto) {
        orderService.cancelOrders(dto.getOrderIdx());

        ApiResponse<Object> response = ApiResponse.success(OK, "주문이 취소되었습니다.");
        return ResponseEntity.ok(response);
    }
}
