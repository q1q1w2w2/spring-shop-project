package com.example.demo1.exception;

import com.example.demo1.dto.ErrorResponseDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 예기치 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception e) {
        return createErrorResponse(e, INTERNAL_SERVER_ERROR, "예기치 못한 오류가 발생했습니다: " + e.getMessage());
    }

    // 사용자 인증 인가에 대한 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException e) {
        return createErrorResponse(e, FORBIDDEN, "권한이 없습니다.");
    }

    @ExceptionHandler(UserBannedException.class)
    public ResponseEntity<ErrorResponseDto> handleUserBannedException(UserBannedException e) {
        return createErrorResponse(e, FORBIDDEN, "차단당한 계정입니다.");
    }

    // Bean Validation 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
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
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(UserNotFoundException e) {
        return createErrorResponse(e, NOT_FOUND, "사용자를 찾을 수 없습니다.");
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistException(UserAlreadyExistException e) {
        return createErrorResponse(e, CONFLICT, e.getMessage());
    }

    // 인증 관련 예외 처리
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException e) {
        return createErrorResponse(e, UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸습니다.");
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleTokenValidationException(TokenValidationException e) {
        return createErrorResponse(e, UNAUTHORIZED, e.getMessage());
    }

    // 상품 관련 예외 처리
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleItemNotFoundException(ItemNotFoundException e) {
        return createErrorResponse(e, BAD_REQUEST, "상품을 찾을 수 없습니다.");
    }

    @ExceptionHandler(ItemOwnershipException.class)
    public ResponseEntity<ErrorResponseDto> handleItemOwnershipException(ItemOwnershipException e) {
        return createErrorResponse(e, BAD_REQUEST, "상품을 등록한 사용자가 아닙니다.");
    }

    @ExceptionHandler(ItemAlreadyDeleteException.class)
    public ResponseEntity<ErrorResponseDto> handleItemAlreadyDeleteException(ItemAlreadyDeleteException e) {
        return createErrorResponse(e, CONFLICT, e.getMessage());
    }

    // 상품 이미지 관련 예외 처리
    @ExceptionHandler(InvalidImageSequenceException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidImageSequenceException(InvalidImageSequenceException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }

    // 카테고리 관련 예외 처리
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCategoryNotFoundException(CategoryNotFoundException e) {
        return createErrorResponse(e, BAD_REQUEST, "카테고리를 찾을 수 없습니다.");
    }

    @ExceptionHandler(CategoryAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleCategoryAlreadyExist(CategoryAlreadyExistException e) {
        return createErrorResponse(e, CONFLICT, "이미 존재하는 카테고리입니다.");
    }

    // 주문 관련 예외 처리
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleOrderNotFoundException(OrderNotFoundException e) {
        return createErrorResponse(e, BAD_REQUEST, "주문을 찾을 수 없습니다.");
    }

    @ExceptionHandler(OrderStepException.class)
    public ResponseEntity<ErrorResponseDto> handleOrderStepException(OrderStepException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidQuantityException(InvalidQuantityException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }

    // 리뷰 관련 예외 처리
    @ExceptionHandler(ReviewNotAllowedException.class)
    public ResponseEntity<ErrorResponseDto> handleReviewNotAllowedException(ReviewNotAllowedException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleReviewNotFoundException(ReviewNotFoundException e) {
        return createErrorResponse(e, BAD_REQUEST, "리뷰를 찾을 수 없습니다.");
    }

    // 장바구니 관련 예외 처리
    @ExceptionHandler(ItemCartNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleItemCartNotFoundException(ItemCartNotFoundException e) {
        return createErrorResponse(e, BAD_REQUEST, e.getMessage());
    }

    // ResponseEntity 반환 메서드
    private ResponseEntity<ErrorResponseDto> createErrorResponse(Exception e, HttpStatus status, String message) {
        log.error("[{} 발생]", e.getClass().getSimpleName());
        ErrorResponseDto response = new ErrorResponseDto(status.value(), status.name(), message);
        return ResponseEntity.status(status).body(response);
    }

}
