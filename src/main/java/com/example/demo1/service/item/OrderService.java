package com.example.demo1.service.item;

import com.example.demo1.domain.item.Item;
import com.example.demo1.domain.item.OrderLog;
import com.example.demo1.domain.item.Orders;
import com.example.demo1.domain.user.User;
import com.example.demo1.dto.order.CreateOrdersDto;
import com.example.demo1.dto.order.OrderResult;
import com.example.demo1.dto.order.OrderSearch;
import com.example.demo1.exception.Item.item.ItemAlreadyDeleteException;
import com.example.demo1.exception.Item.item.ItemNotFoundException;
import com.example.demo1.exception.order.InvalidQuantityException;
import com.example.demo1.exception.order.OrderNotFoundException;
import com.example.demo1.exception.order.OrderStepException;
import com.example.demo1.repository.item.ItemRepository;
import com.example.demo1.repository.order.OrderLogRepository;
import com.example.demo1.repository.order.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.demo1.util.constant.Constants.*;

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

        for (CreateOrdersDto dto : dtoList) {
            Item item = itemRepository.findById(dto.getItemIdx())
                    .orElseThrow(ItemNotFoundException::new);
            // 삭제된 상품 검증
            if (item.getState() == DELETED_ITEM_STATE) {
                throw new ItemAlreadyDeleteException("삭제된 상품이 포함되어 있습니다: " + dto.getItemIdx());
            }
            // 수량 검증
            if (dto.getQuantity() < 1) {
                throw new InvalidQuantityException("1개 이상의 수량을 입력해야 합니다.");
            }
        }

        // order 생성
        Orders orders = Orders.createOrders(user);
        ordersRepository.save(orders);

        // orderLog 생성
        List<OrderLog> orderLogs = new ArrayList<>();
        for (CreateOrdersDto dto : dtoList) {
            Item item = itemRepository.findById(dto.getItemIdx())
                    .orElseThrow(ItemNotFoundException::new);

            OrderLog orderLog = OrderLog.createOrderLog(orders, item, dto.getQuantity());
            orderLogRepository.save(orderLog);

            orderLogs.add(orderLog);
        }

        return new OrderResult(orders, orderLogs);
    }

    // orders 조회
    public List<Orders> findAll(OrderSearch orderSearch) {
        return ordersRepository.findAll(orderSearch);
    }


    public List<Map<String, Object>> findAll(OrderSearch orderSearch, User user) {
        List<Map<String, Object>> response = new ArrayList<>();
        List<Orders> orderList = ordersRepository.findAll(orderSearch, user);

        for (Orders orders : orderList) {
            Map<String, Object> map = new HashMap<>();
//            List<OrderLog> orderLogs = orderLogRepository.findByOrders(orders)
//                    .orElseThrow(OrderNotFoundException::new);
            map.put("orderIdx", orders.getIdx());
            map.put("step", orders.getStep());
            map.put("orderTime", orders.getCreatedAt());

            List<Map<String, Object>> orderLogList = new ArrayList<>();
            for (OrderLog orderLog : orders.getOrderLogs()) {
                Map<String, Object> orderLogMap = new HashMap<>();
                orderLogMap.put("itemIdx", orderLog.getItem().getIdx());
                orderLogMap.put("itemName", orderLog.getItem().getItemName());
                orderLogMap.put("itemPrice", orderLog.getItem().getPrice());

                orderLogMap.put("orderLogIdx", orderLog.getIdx());
                orderLogMap.put("ea", orderLog.getEa());
                orderLogMap.put("review", orderLog.getReview());

                orderLogList.add(orderLogMap);
            }

            map.put("orderLogs", orderLogList);
            response.add(map);
        }

        return response;
    }

    // 주문 상태 변경
    @Transactional
    public void startOrders(Long ordersIdx) {
        updateStep(ordersIdx, ORDER_STEP_START, "이미 배송이 시작된 주문입니다.");
    }

    @Transactional
    public void completeOrders(Long ordersIdx) {
        updateStep(ordersIdx, ORDER_STEP_COMP, "이미 배송이 완료된 주문입니다.");
    }

    @Transactional
    public void cancelOrders(Long ordersIdx) {
        updateStep(ordersIdx, ORDER_STEP_CANCEL, "이미 취소된 주문입니다.");
    }

    private void updateStep(Long ordersIdx, int step, String message) {
        Orders orders = getOrders(ordersIdx);
        if (orders.getStep() == step) {
            throw new OrderStepException(message);
        }
        orders.updateStep(step);
    }

    private Orders getOrders(Long ordersIdx) {
        return ordersRepository.findById(ordersIdx)
                .orElseThrow(OrderNotFoundException::new);
    }
}
