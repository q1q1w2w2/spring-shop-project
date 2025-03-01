package com.example.demo1.service.facade;

import com.example.demo1.dto.order.*;
import com.example.demo1.entity.item.OrderLog;
import com.example.demo1.entity.item.Orders;
import com.example.demo1.entity.user.User;
import com.example.demo1.service.item.OrderLogService;
import com.example.demo1.service.item.OrderService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderFacade {

    private final OrderService orderService;
    private final UserService userService;
    private final OrderLogService orderLogService;

    @Transactional
    public void createOrder(List<CreateOrdersDto> orders) {
        orderService.saveOrders(getCurrentUser(), orders);
    }

    public OrderListResponseDto getOrdersForAdmin(OrderSearch orderSearch) {
        List<Orders> orders = orderService.findAll(orderSearch);
        return orders.stream()
                .map(order -> new OrderListResponseDto(
                        new OrderResponseDto(order),
                        order.getOrderLogs().stream()
                                .map(OrderLogDetailDto::new)
                                .collect(Collectors.toList())
                ))
                .findFirst()
                .orElseGet(OrderListResponseDto::new);
    }

    public List<OrderDetailForPersonal> getOrdersForPersonal(OrderSearch orderSearch) {
        return orderService.findAll(orderSearch, getCurrentUser());
    }

    public List<OrderLogResponseDto> getOrderDetail(Long orderIdx) {
        List<OrderLog> orderLogs = orderLogService.findByOrderIdx(orderIdx);
        return orderLogs.stream()
                .map(OrderLogResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void startOrder(Long orderIdx) {
        orderService.startOrders(orderIdx);
    }

    @Transactional
    public void completeOrder(Long orderIdx) {
        orderService.completeOrders(orderIdx);
    }

    @Transactional
    public void cancelOrder(Long orderIdx) {
        orderService.cancelOrders(orderIdx);
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }
}
