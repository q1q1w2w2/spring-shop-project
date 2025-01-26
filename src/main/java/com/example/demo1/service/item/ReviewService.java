package com.example.demo1.service.item;

import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.item.OrderLog;
import com.example.demo1.entity.item.Orders;
import com.example.demo1.entity.item.Review;
import com.example.demo1.dto.order.CreateReviewDto;
import com.example.demo1.dto.order.ReviewSearch;
import com.example.demo1.exception.Item.review.ReviewNotAllowedException;
import com.example.demo1.exception.Item.review.ReviewNotFoundException;
import com.example.demo1.exception.order.OrderNotFoundException;
import com.example.demo1.repository.order.OrderLogRepository;
import com.example.demo1.repository.item.ReviewRepository;
import com.example.demo1.util.constant.OrderStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.demo1.util.constant.Constants.*;
import static com.example.demo1.util.constant.OrderStep.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderLogRepository orderLogRepository;

    @Transactional
    public Review saveReview(CreateReviewDto dto, Long userId) {
        OrderLog orderLog = orderLogRepository.findById(dto.getOrderLogIdx())
                .orElseThrow(OrderNotFoundException::new);

        Orders orders = orderLog.getOrders();

        if (!orders.getUser().getId().equals(userId)) {
            throw new ReviewNotAllowedException("해당 상품을 주문한 사용자가 아닙니다.");
        }
        if (orders.getStep() != DELIVERY_COMP.getValue()) {
            throw new ReviewNotAllowedException("배송 완료 상태에서만 리뷰를 달 수 있습니다.");
        }
        if (orderLog.getReview() == REVIEW_COMP) {
            throw new ReviewNotAllowedException("이미 리뷰를 작성한 주문입니다.");
        }

        orderLog.updateReviewStatus(REVIEW_COMP);

        Review review = Review.builder()
                .orderLog(orderLog)
                .review(dto.getReview())
                .score(dto.getScore())
                .build();

        return reviewRepository.save(review);
    }

    public List<Review> getReview(ReviewSearch reviewSearch) {
        return reviewRepository.findAll(reviewSearch);
    }

    public List<Review> getReviewByItemIdx(Item item) {
        return reviewRepository.findAllByItem(item);
    }

    @Transactional
    public void blindReview(Long reviewIdx) {
        Review review = findReviewByIdx(reviewIdx);
        review.blind(REVIEW_BLIND);
    }

    @Transactional
    public void publishReview(Long reviewIdx) {
        Review review = findReviewByIdx(reviewIdx);
        review.blind(REVIEW_BLIND_CANCEL);
    }

    public Review findReviewByIdx(Long reviewIdx) {
        return reviewRepository.findById(reviewIdx)
                .orElseThrow(ReviewNotFoundException::new);
    }
}
