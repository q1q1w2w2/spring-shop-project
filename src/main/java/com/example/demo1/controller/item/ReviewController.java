package com.example.demo1.controller.item;

import com.example.demo1.dto.common.ApiResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final ItemService itemService;

    @PostMapping("/api/item/review")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(@Validated @RequestBody CreateReviewDto dto) {
        User user = userService.getCurrentUser();
        Review review = reviewService.saveReview(dto, user.getId());

        ApiResponse<ReviewResponseDto> response = ApiResponse.success(OK, new ReviewResponseDto(review));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/item/review/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<Object>>> getReview(@ModelAttribute ReviewSearch reviewSearch) {
        List<Review> reviewList = reviewService.getReview(reviewSearch);
        List<Object> reviewDtoList = new ArrayList<>();
        for (Review review : reviewList) {
            reviewDtoList.add(new GetReviewListDto(review));
        }

        ApiResponse<List<Object>> response = ApiResponse.success(OK, reviewDtoList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/item/review")
    public ResponseEntity getItemReview(@Validated @RequestBody ItemRequestDto dto) {
        Item item = itemService.findByIdx(dto.getItemIdx());
        List<Review> reviewList = reviewService.getReviewByItemIdx(item);
        List<Object> reviewDtoList = new ArrayList<>();
        for (Review review : reviewList) {
            GetReviewListDto reviewListDto = new GetReviewListDto(review);
            reviewDtoList.add(reviewListDto);
        }

        ApiResponse<List<Object>> response = ApiResponse.success(OK, reviewDtoList);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/item/review/blind")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity reviewBlind(@RequestParam Long reviewIdx) {
        reviewService.blindReview(reviewIdx);

        ApiResponse<Object> response = ApiResponse.success(OK, "후기를 차단했습니다.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/item/review/blind/cancel")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity reviewBlindCancel(@RequestParam Long reviewIdx) {
        reviewService.blindReviewCancel(reviewIdx);

        ApiResponse<Object> response = ApiResponse.success(OK, "차단을 해제했습니다.");
        return ResponseEntity.ok(response);
    }
}
