package com.example.demo1.dto.order;

import com.example.demo1.domain.item.Review;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetReviewListDto {

    private Long orderLogIdx;
    private int score;
    private String review;
    private LocalDateTime createdAt;
    private String username;

    private int quantity;
    private Long reviewIdx;
    private String itemName;
    private int blind;

    public GetReviewListDto(Review review) {
        this.orderLogIdx = review.getOrderLog().getIdx();
        this.score = review.getScore();
        this.review = review.getReview();
        this.createdAt = review.getCreatedAt();
        this.username = review.getOrderLog().getOrders().getUser().getUsername();
        this.quantity = review.getOrderLog().getEa();
        this.reviewIdx = review.getIdx();
        this.itemName = review.getOrderLog().getItem().getItemName();
        this.blind = review.getBlind();
    }
}
