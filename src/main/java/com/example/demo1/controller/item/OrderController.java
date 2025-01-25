package com.example.demo1.controller.item;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.dto.order.*;
import com.example.demo1.service.facade.OrderFacade;
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
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    @PostMapping("/api/order")
    public ResponseEntity<ApiResponse<Object>> createOrders(@Valid @RequestBody List<CreateOrdersDto> ordersDto) {
        orderFacade.createOrder(ordersDto);
        return createResponse(OK, "주문이 완료되었습니다.");
    }

    @GetMapping("/order/list/admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderListResponseDto>> orderListAdmin(OrderSearch orderSearch) {
        OrderListResponseDto orderListResponseDto = orderFacade.getOrdersForAdmin(orderSearch);
        return createResponse(OK, orderListResponseDto);
    }

    @GetMapping("/order/list")
    public ResponseEntity<ApiResponse<List<OrderDetailForPersonal>>> orderList(OrderSearch orderSearch) {
        List<OrderDetailForPersonal> orders = orderFacade.getOrdersForPersonal(orderSearch);
        return createResponse(OK, orders);
    }

    @GetMapping("/order/list/{orderIdx}")
    public ResponseEntity<ApiResponse<List<OrderLogResponseDto>>> orderDetail(@PathVariable Long orderIdx) {
        List<OrderLogResponseDto> ordersDetail = orderFacade.getOrderDetail(orderIdx);
        return createResponse(OK, ordersDetail);
    }

    @PatchMapping("/api/order/start")
    public ResponseEntity<ApiResponse<Object>> startOrder(@Valid @RequestBody OrderRequestDto dto) {
        orderFacade.startOrder(dto.getOrderIdx());
        return createResponse(OK, "배송 시작 상태로 변경되었습니다.");
    }

    @PatchMapping("/api/order/complete")
    public ResponseEntity<ApiResponse<Object>> completeOrder(@Valid @RequestBody OrderRequestDto dto) {
        orderFacade.completeOrder(dto.getOrderIdx());
        return createResponse(OK, "배송 완료 상태로 변경되었습니다.");
    }

    @PatchMapping("/api/order/cancel")
    public ResponseEntity<ApiResponse<Object>> cancelOrder(@Valid @RequestBody OrderRequestDto dto) {
        orderFacade.cancelOrder(dto.getOrderIdx());
        return createResponse(OK, "주문이 취소되었습니다.");
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
