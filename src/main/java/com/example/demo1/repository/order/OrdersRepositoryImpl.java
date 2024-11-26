package com.example.demo1.repository.order;

import com.example.demo1.domain.item.Orders;
import com.example.demo1.domain.item.QItem;
import com.example.demo1.domain.item.QOrderLog;
import com.example.demo1.domain.item.QOrders;
import com.example.demo1.domain.user.QUser;
import com.example.demo1.domain.user.User;
import com.example.demo1.dto.order.OrderSearch;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class OrdersRepositoryImpl implements OrdersRepositoryCustom {

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Orders> findAll(OrderSearch orderSearch) {
        return findAllWithCondition(orderSearch, null);
    }

    @Override
    public List<Orders> findAll(OrderSearch orderSearch, User user) {
        return findAllWithCondition(orderSearch, user);
    }

    private List<Orders> findAllWithCondition(OrderSearch orderSearch, User user) {
        QOrders o = QOrders.orders;
        QUser u = QUser.user;

        QOrderLog ol = QOrderLog.orderLog;
        QItem i = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();

        if (user != null) {
            builder.and(o.user.eq(user));
        }
        if (orderSearch.getLoginId() != null) {
            builder.and(o.user.loginId.contains(orderSearch.getLoginId()));
        }
        if (orderSearch.getStep() != null && orderSearch.getStep() != 0) {
            builder.and(o.step.eq(orderSearch.getStep()));
        }

        return queryFactory
                .selectFrom(o)
                .join(o.user, u)
                .leftJoin(o.orderLogs, ol)
                .fetchJoin()
                .where(builder)
                .orderBy(o.idx.desc())
                .distinct()
                .limit(1000)
                .fetch();
    }

}
