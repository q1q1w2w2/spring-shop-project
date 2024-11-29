package com.example.demo1.web.item;

import com.example.demo1.domain.item.OrderLog;
import com.example.demo1.domain.item.Orders;
import com.example.demo1.domain.user.User;
import com.example.demo1.dto.order.*;
import com.example.demo1.service.item.OrderLogService;
import com.example.demo1.service.item.OrderService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final OrderLogService orderLogService;

    // 주문 생성
    @PostMapping("/api/order")
    public ResponseEntity<Map> createOrders(@Validated @RequestBody List<CreateOrdersDto> dtoList) {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
        orderService.saveOrders(user, dtoList);
        return ResponseEntity.ok(Map.of("message", "주문이 완료되었습니다."));
    }

    // 주문 목록(관리자)
    @GetMapping("/order/list/admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<Object>> orderListAdmin(OrderSearch orderSearch) {
        long start = System.currentTimeMillis();

        List<Orders> list = orderService.findAll(orderSearch);
        List<Object> response = new ArrayList<>();

        for (Orders order : list) {
            Map<String, Object> orderResponse = new HashMap<>();
            orderResponse.put("orderInfo", new OrderResponseDto(order));


//            ArrayList<OrderLogResponseDto> orderLogResponse = new ArrayList<>();
//            List<OrderLog> orderLogs = order.getOrderLogs();
//            for (OrderLog orderLog : orderLogs) {
//                orderLogResponse.add(new OrderLogResponseDto(orderLog));
//            }

            List<Map<String, Object>> orderLogResponse = new ArrayList<>();

            for (OrderLog orderLog : order.getOrderLogs()) {
                Map<String, Object> map = new HashMap<>();
                map.put("itemIdx", orderLog.getItem().getIdx());
                map.put("itemName", orderLog.getItem().getItemName());
                map.put("ea", orderLog.getEa());
                orderLogResponse.add(map);
            }
            orderResponse.put("orderLogs", orderLogResponse);
            response.add(orderResponse);
        }
        long end = System.currentTimeMillis();
        log.info("response time: {} ms", end - start);
        return ResponseEntity.ok(response);
    }

    // 주문 조회(개인)
    @GetMapping("/order/list")
    public ResponseEntity<List<Map<String, Object>>> orderList(OrderSearch orderSearch) {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
        List<Map<String, Object>> response = orderService.findAll(orderSearch, user);
        return ResponseEntity.ok(response);
    }

    // 주문 상세
    @GetMapping("/order/list/{orderIdx}")
    public ResponseEntity<List<OrderLogResponseDto>> orderDetail(@PathVariable Long orderIdx) {
        List<OrderLog> orderLogs = orderLogService.findByOrderIdx(orderIdx);
        List<OrderLogResponseDto> response = new ArrayList<>();
        for (OrderLog orderLog : orderLogs) {
            response.add(new OrderLogResponseDto(orderLog));

        }
        return ResponseEntity.ok(response);
    }

    // 배송 시작 상태
    @PatchMapping("/api/order/start")
    public ResponseEntity<Map> startOrder(@Validated @RequestBody OrderRequestDto dto) {
        orderService.startOrders(dto.getOrderIdx());
        return ResponseEntity.ok(Map.of("message", "배송 시작 상태로 변경되었습니다."));
    }

    // 배송 완료 상태
    @PatchMapping("/api/order/complete")
    public ResponseEntity<Map> completeOrder(@Validated @RequestBody OrderRequestDto dto) {
        orderService.completeOrders(dto.getOrderIdx());
        return ResponseEntity.ok(Map.of("message", "배송 완료 상태로 변경되었습니다."));
    }

    // 주문 취소 상태
    @PatchMapping("/api/order/cancel")
    public ResponseEntity<Map> cancelOrder(@Validated @RequestBody OrderRequestDto dto) {
        orderService.cancelOrders(dto.getOrderIdx());
        return ResponseEntity.ok(Map.of("message", "주문이 취소되었습니다."));
    }
}
