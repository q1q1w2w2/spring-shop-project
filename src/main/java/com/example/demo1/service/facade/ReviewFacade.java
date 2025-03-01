package com.example.demo1.service.facade;

import com.example.demo1.dto.item.ItemRequestDto;
import com.example.demo1.dto.order.*;
import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.item.Review;
import com.example.demo1.entity.user.User;
import com.example.demo1.service.item.ItemService;
import com.example.demo1.service.item.ReviewService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewFacade {

    private final UserService userService;
    private final ReviewService reviewService;
    private final ItemService itemService;

    @Transactional
    public ReviewResponseDto saveReview(CreateReviewDto reviewDto) {
        Review savedReview = reviewService.saveReview(reviewDto, getCurrentUser().getId());
        return new ReviewResponseDto(savedReview);
    }

    public List<GetReviewListDto> getReviews(ReviewSearch reviewSearch) {
        List<Review> reviews = reviewService.getReview(reviewSearch);
        return reviews.stream()
                .map(GetReviewListDto::new)
                .toList();
    }

    public List<GetReviewListDto> getItemReviews(ItemRequestDto itemRequestDto) {
        Item item = itemService.findByIdx(itemRequestDto.getItemIdx());
        List<Review> reviewList = reviewService.getReviewByItemIdx(item);
        return reviewList.stream()
                .map(GetReviewListDto::new)
                .toList();
    }

    @Transactional
    public void blindReview(Long reviewIdx) {
        reviewService.blindReview(reviewIdx);
    }

    @Transactional
    public void publishReview(Long reviewIdx) {
        reviewService.publishReview(reviewIdx);
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }
}
