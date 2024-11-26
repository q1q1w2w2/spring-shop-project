package com.example.demo1.repository.order;

import com.example.demo1.domain.item.Orders;
import com.example.demo1.domain.user.User;
import com.example.demo1.dto.order.OrderSearch;

import java.util.List;

public interface OrdersRepositoryCustom {
    List<Orders> findAll(OrderSearch orderSearch);
    List<Orders> findAll(OrderSearch orderSearch, User user);
}
