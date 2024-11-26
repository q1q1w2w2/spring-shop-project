package com.example.demo1.repository.order;

import com.example.demo1.domain.item.OrderLog;
import com.example.demo1.domain.item.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
    Optional<List<OrderLog>> findByOrders(Orders orders);
}
