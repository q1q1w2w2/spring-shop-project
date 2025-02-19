package com.example.demo1.exception;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.exception.Item.category.CategoryAlreadyExistException;
import com.example.demo1.exception.Item.category.CategoryNotFoundException;
import com.example.demo1.exception.Item.item.ItemAlreadyDeleteException;
import com.example.demo1.exception.Item.item.ItemCartNotFoundException;
import com.example.demo1.exception.Item.item.ItemNotFoundException;
import com.example.demo1.exception.Item.item.ItemOwnershipException;
import com.example.demo1.exception.Item.itemImage.InvalidImageSequenceException;
import com.example.demo1.exception.Item.review.ReviewNotAllowedException;
import com.example.demo1.exception.Item.review.ReviewNotFoundException;
import com.example.demo1.exception.order.InvalidQuantityException;
import com.example.demo1.exception.order.OrderNotFoundException;
import com.example.demo1.exception.order.OrderStepException;
import com.example.demo1.exception.token.TokenValidationException;
import com.example.demo1.exception.user.UserAlreadyExistException;
import com.example.demo1.exception.user.UserBannedException;
import com.example.demo1.exception.user.UserNotFoundException;
import com.example.demo1.util.common.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.example.demo1.util.common.ApiResponseUtil.*;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 예기치 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception e) {
        return createErrorResponse(e, INTERNAL_SERVER_ERROR, "예기치 못한 오류가 발생했습니다: " + e.getMessage());
    }

    // 사용자 인증 인가에 대한 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException e) {
        return createErrorResponse(e, FORBIDDEN, "권한이 없습니다.");
    }

    @ExceptionHandler(UserBannedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserBannedException(UserBannedException e) {
        return createErrorResponse(e, FORBIDDEN, "차단당한 계정입니다.");
    }

    // Bean Validation 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .findFirst()
                .orElse(e.getMessage());
        return createErrorResponse(e, BAD_REQUEST, message);
    }

    // 사용자 관련 예외 처리
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(UserNotFoundException e) {
        return createErrorResponse(e, NOT_FOUND, "사용자를 찾을 수 없습니다.");
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserAlreadyExistException(UserAlreadyExistException e) {
        return createErrorResponse(e, CONFLICT, e.getMessage());
    }

    // 인증 관련 예외 처리
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException e) {
        return createErrorResponse(e, UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸습니다.");
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenValidationException(TokenValidationException e) {
        return createErrorResponse(e, UNAUTHORIZED, e.getMessage());
    }

    // 상품 관련 예외 처리
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleItemNotFoundException(ItemNotFoundException e) {
        return createErrorResponse(e, BAD_REQUEST, "상품을 찾을 수 없습니다.");
    }

    @ExceptionHandler(ItemOwnershipException.class)
    public ResponseEntity<ApiResponse<Object>> handleItemOwnershipException(ItemOwnershipException e) {
        return createErrorResponse(e, BAD_REQUEST, "상품을 등록한 사용자가 아닙니다.");
    }

    @ExceptionHandler(ItemAlreadyDeleteException.class)
    public ResponseEntity<ApiResponse<Object>> handleItemAlreadyDeleteException(ItemAlreadyDeleteException e) {
        return createErrorResponse(e, CONFLICT, e.getMessage());
    }

    // 상품 이미지 관련 예외 처리
    @ExceptionHandler(InvalidImageSequenceException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidImageSequenceException(InvalidImageSequenceException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }

    // 카테고리 관련 예외 처리
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleCategoryNotFoundException(CategoryNotFoundException e) {
        return createErrorResponse(e, BAD_REQUEST, "카테고리를 찾을 수 없습니다.");
    }

    @ExceptionHandler(CategoryAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Object>> handleCategoryAlreadyExist(CategoryAlreadyExistException e) {
        return createErrorResponse(e, CONFLICT, "이미 존재하는 카테고리입니다.");
    }

    // 주문 관련 예외 처리
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleOrderNotFoundException(OrderNotFoundException e) {
        return createErrorResponse(e, BAD_REQUEST, "주문을 찾을 수 없습니다.");
    }

    @ExceptionHandler(OrderStepException.class)
    public ResponseEntity<ApiResponse<Object>> handleOrderStepException(OrderStepException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidQuantityException(InvalidQuantityException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }

    // 리뷰 관련 예외 처리
    @ExceptionHandler(ReviewNotAllowedException.class)
    public ResponseEntity<ApiResponse<Object>> handleReviewNotAllowedException(ReviewNotAllowedException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleReviewNotFoundException(ReviewNotFoundException e) {
        return createErrorResponse(e, BAD_REQUEST, "리뷰를 찾을 수 없습니다.");
    }

    // 장바구니 관련 예외 처리
    @ExceptionHandler(ItemCartNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleItemCartNotFoundException(ItemCartNotFoundException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }
}
