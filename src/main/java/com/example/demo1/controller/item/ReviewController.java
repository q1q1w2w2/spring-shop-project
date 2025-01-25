package com.example.demo1.controller.item;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.dto.item.ItemRequestDto;
import com.example.demo1.dto.order.CreateReviewDto;
import com.example.demo1.dto.order.GetReviewListDto;
import com.example.demo1.dto.order.ReviewResponseDto;
import com.example.demo1.dto.order.ReviewSearch;
import com.example.demo1.service.facade.ReviewFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewFacade reviewFacade;

    @PostMapping("/api/item/review")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(@Valid @RequestBody CreateReviewDto reviewDto) {
        ReviewResponseDto review = reviewFacade.saveReview(reviewDto);
        return createResponse(OK, review);
    }

    @GetMapping("/item/review/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<GetReviewListDto>>> getReview(@ModelAttribute ReviewSearch reviewSearch) {
        List<GetReviewListDto> reviews = reviewFacade.getReviews(reviewSearch);
        return createResponse(OK, reviews);
    }

    @GetMapping("/item/review")
    public ResponseEntity<ApiResponse<List<GetReviewListDto>>> getItemReview(@Valid @RequestBody ItemRequestDto itemRequestDto) {
        List<GetReviewListDto> itemReviews = reviewFacade.getItemReviews(itemRequestDto);
        return createResponse(OK, itemReviews);
    }

    @PatchMapping("/item/review/blind")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> reviewBlind(@RequestParam Long reviewIdx) {
        reviewFacade.blindReview(reviewIdx);
        return createResponse(OK, "후기를 차단했습니다.");
    }

    @PatchMapping("/item/review/blind/cancel")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> reviewBlindCancel(@RequestParam Long reviewIdx) {
        reviewFacade.publishReview(reviewIdx);
        return createResponse(OK, "차단을 해제했습니다.");
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, String message) {
        ApiResponse<T> response = ApiResponse.success(status, message);
        return ResponseEntity.status(status).body(response);
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, T data) {
        ApiResponse<T> response = ApiResponse.success(status, data);
        return ResponseEntity.status(status).body(response);
    }
}
