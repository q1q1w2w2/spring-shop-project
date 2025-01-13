package com.example.demo1.repository.item;

import com.example.demo1.entity.item.*;
import com.example.demo1.dto.order.ReviewSearch;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom{

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Review> findAll(ReviewSearch reviewSearch) {
        QItem i = QItem.item;
//        QCategory c = QCategory.category;
        QReview r = QReview.review1;
        QOrderLog ol = QOrderLog.orderLog;

        BooleanBuilder builder = new BooleanBuilder();

        //  itemIdx 0일 때 전체조회
        if (reviewSearch.getItemIdx() != null && reviewSearch.getItemIdx() != 0) {
            builder.and(i.idx.eq(reviewSearch.getItemIdx()));
        }

        JPAQuery<Review> query = queryFactory
                .selectFrom(r)
                .join(r.orderLog, ol).fetchJoin()
                .join(ol.item, i).fetchJoin()
//                .join(i.category, c)
                .where(builder)
                .distinct()
                .limit(1000);

        // sortByScore 0오름차순 1내림차순, 비어있으면 내림차순
        if (reviewSearch.getSortByScore() == null || reviewSearch.getSortByScore() == 1) {
            query.orderBy(r.score.desc());
        } else {
            query.orderBy(r.score.asc());
        }

        return query.fetch();
    }

    @Override
    public List<Review> findAllByItem(Item item) {
        QItem i = QItem.item;
        QOrderLog ol = QOrderLog.orderLog;
        QReview r = QReview.review1;

        return queryFactory
                .selectFrom(r)
                .join(r.orderLog, ol)
                .join(ol.item, i)
                .where(i.idx.eq(item.getIdx()))
                .limit(100)
                .fetch();
    }
}
