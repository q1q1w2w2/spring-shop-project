package com.example.demo1.repository.order;

import com.example.demo1.domain.item.Orders;
import com.example.demo1.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long>, OrdersRepositoryCustom {
}
