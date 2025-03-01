package com.example.demo1.service.item;

import com.example.demo1.dto.order.*;
import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.item.OrderLog;
import com.example.demo1.entity.item.Orders;
import com.example.demo1.entity.user.User;
import com.example.demo1.exception.Item.item.ItemAlreadyDeleteException;
import com.example.demo1.exception.Item.item.ItemNotFoundException;
import com.example.demo1.exception.order.InvalidQuantityException;
import com.example.demo1.exception.order.OrderNotFoundException;
import com.example.demo1.exception.order.OrderStepException;
import com.example.demo1.repository.item.ItemRepository;
import com.example.demo1.repository.order.OrderLogRepository;
import com.example.demo1.repository.order.OrdersRepository;
import com.example.demo1.util.constant.OrderStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo1.util.constant.ItemStatus.*;
import static com.example.demo1.util.constant.OrderStep.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final ItemRepository itemRepository;
    private final OrdersRepository ordersRepository;
    private final OrderLogRepository orderLogRepository;

    @Transactional
    public OrderResult saveOrders(User user, List<CreateOrdersDto> dtoList) {
        Map<Long, Item> itemMap = dtoList.stream()
                .map(dto -> itemRepository.findById(dto.getItemIdx())
                        .orElseThrow(ItemNotFoundException::new))
                .peek(item -> {
                    if (item.getStatus() == DELETED.getValue()) {
                        throw new ItemAlreadyDeleteException("삭제된 상품이 포함되어 있습니다: " + item.getIdx());
                    }
                })
                .collect(Collectors.toMap(Item::getIdx, item -> item));

        if (dtoList.stream().anyMatch(dto -> dto.getQuantity() < 1)) {
            throw new InvalidQuantityException("1개 이상의 수량을 입력해야 합니다.");
        }

        Orders orders = Orders.createOrders(user);
        ordersRepository.save(orders);

        List<OrderLog> orderLogs = dtoList.stream()
                .map(dto -> OrderLog.createOrderLog(orders, itemMap.get(dto.getItemIdx()), dto.getQuantity()))
                .map(orderLogRepository::save)
                .toList();

        return new OrderResult(orders, orderLogs);
    }


    public List<Orders> findAll(OrderSearch orderSearch) {
        return ordersRepository.findAll(orderSearch);
    }

    public List<OrderDetailForPersonal> findAll(OrderSearch orderSearch, User user) {
        return ordersRepository.findAll(orderSearch, user).stream()
                .map(orders -> new OrderDetailForPersonal(
                        orders,
                        orders.getOrderLogs().stream()
                                .map(OrderLogDetailForPersonal::new)
                                .toList()
                ))
                .toList();
    }

    @Transactional
    public void startOrders(Long ordersIdx) {
        updateStep(ordersIdx, ORDER_START, "이미 배송이 시작된 주문입니다.");
    }

    @Transactional
    public void completeOrders(Long ordersIdx) {
        updateStep(ordersIdx, DELIVERY_COMP, "이미 배송이 완료된 주문입니다.");
    }

    @Transactional
    public void cancelOrders(Long ordersIdx) {
        updateStep(ordersIdx, ORDER_CANCEL, "이미 취소된 주문입니다.");
    }

    private void updateStep(Long ordersIdx, OrderStep step, String message) {
        Orders orders = getOrders(ordersIdx);
        if (orders.getStep() == step.getValue()) {
            throw new OrderStepException(message);
        }
        orders.updateStep(step);
    }

    private Orders getOrders(Long ordersIdx) {
        return ordersRepository.findById(ordersIdx)
                .orElseThrow(OrderNotFoundException::new);
    }
}
