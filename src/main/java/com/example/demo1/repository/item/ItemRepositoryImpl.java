package com.example.demo1.repository.item;

import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.item.QCategory;
import com.example.demo1.entity.item.QItem;
import com.example.demo1.dto.order.ItemSearch;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Item> findAll(ItemSearch itemSearch) {
        // 검색조건 itemName, category
        QItem i = QItem.item;
        QCategory c = QCategory.category;

        BooleanBuilder builder = new BooleanBuilder();

        if (itemSearch.getItemName() != null) {
            builder.and(i.itemName.contains(itemSearch.getItemName()));
        }

        if (itemSearch.getCategoryIdx() != null && itemSearch.getCategoryIdx() != 0) {
            builder.and(i.category.idx.eq(itemSearch.getCategoryIdx()));
        }

        // state가 0인 경우만 보여주기(판매중)
        builder.and(i.state.eq(0));

        return queryFactory
                .selectFrom(i)
                .join(i.category, c)
                .where(builder)
                .limit(1000)
                .fetch();
    }

}
