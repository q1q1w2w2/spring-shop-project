package com.example.demo1.controller.item;

import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.item.Review;
import com.example.demo1.entity.user.User;
import com.example.demo1.dto.item.ItemRequestDto;
import com.example.demo1.dto.order.CreateReviewDto;
import com.example.demo1.dto.order.GetReviewListDto;
import com.example.demo1.dto.order.ReviewResponseDto;
import com.example.demo1.dto.order.ReviewSearch;
import com.example.demo1.service.item.ItemService;
import com.example.demo1.service.item.ReviewService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final ItemService itemService;

    @PostMapping("/api/item/review")
    public ResponseEntity<ReviewResponseDto> createReview(@Validated @RequestBody CreateReviewDto dto) {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
        Review review = reviewService.saveReview(dto, user.getId());

        ReviewResponseDto response = new ReviewResponseDto(review);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/item/review/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getReview(@ModelAttribute ReviewSearch reviewSearch) {
        List<Review> reviewList = reviewService.getReview(reviewSearch);
        ArrayList<Object> list = new ArrayList<>();
        for (Review review : reviewList) {
            GetReviewListDto dto = new GetReviewListDto(review);
            list.add(dto);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/item/review")
    public ResponseEntity getItemReview(@Validated @RequestBody ItemRequestDto dto) {
        Item item = itemService.findByIdx(dto.getItemIdx());
        List<Review> reviewList = reviewService.getReviewByItemIdx(item);
        ArrayList<Object> list = new ArrayList<>();
        for (Review review : reviewList) {
            GetReviewListDto reviewListDto = new GetReviewListDto(review);
            list.add(reviewListDto);
        }
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/item/review/blind")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity reviewBlind(
            @RequestParam Long reviewIdx
    ) {
        reviewService.blindReview(reviewIdx);
        return ResponseEntity.ok(Map.of("message", "후기를 차단했습니다."));
    }

    @PatchMapping("/item/review/blind/cancel")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity reviewBlindCancel(
            @RequestParam Long reviewIdx
    ) {
        reviewService.blindReviewCancel(reviewIdx);
        return ResponseEntity.ok(Map.of("message", "차단을 해제했습니다."));
    }
}
