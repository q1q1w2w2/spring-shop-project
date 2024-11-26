package com.example.demo1.dto.order;

import com.example.demo1.domain.item.Review;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewResponseDto {

    private Long orderLogIdx;

    private String review;

    private int score;

    public ReviewResponseDto(Review review) {
        this.orderLogIdx = review.getOrderLog().getIdx();
        this.review = review.getReview();
        this.score = review.getScore();
    }
}
