package com.example.demo1.controller.item;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.dto.item.*;
import com.example.demo1.dto.order.ItemSearch;
import com.example.demo1.service.facade.ItemFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemFacade itemFacade;

    @PostMapping("/api/item/save")
    public ResponseEntity<ApiResponse<ItemResponseDto>> saveItem(
            @Valid @RequestPart(value = "item") ItemDto itemDto,
            @RequestPart(value = "image", required = false) List<MultipartFile> images
    ) {
        ItemResponseDto savedItem = itemFacade.saveItem(itemDto, images);
        return createResponse(OK, savedItem);
    }

    @PatchMapping("/api/item/update")
    public ResponseEntity<ApiResponse<ItemResponseDto>> updateItem(
            @Valid @RequestPart ItemUpdateDto itemUpdateDto,
            @RequestPart(value = "addImage", required = false) List<MultipartFile> addImage,
            @RequestPart(value = "deleteImageSeq", required = false) List<Integer> deleteImageSeq,
            @RequestPart(value = "updateImageSeq", required = false) List<Integer> updateImageSeq,
            @RequestPart(value = "changeImage", required = false) List<MultipartFile> changeImage
    ) {
        ItemResponseDto updatedItem = itemFacade.update(itemUpdateDto, addImage, deleteImageSeq, updateImageSeq, changeImage);
        return createResponse(OK, updatedItem);
    }

    @GetMapping("/item/list")
    public ResponseEntity<ApiResponse<List<ItemListDto>>> getAllItems(@ModelAttribute ItemSearch itemSearch) {
        List<ItemListDto> allItems = itemFacade.getAllItems(itemSearch);
        return createResponse(OK, allItems);
    }

    @GetMapping("/item")
    public ResponseEntity<ApiResponse<ItemResponseDto>> itemDetail(@RequestParam Long itemIdx) {
        ItemResponseDto item = itemFacade.getItem(itemIdx);
        return createResponse(OK, item);
    }

    @PatchMapping("/api/item/delete")
    public ResponseEntity<ApiResponse<Object>> deleteItem(@RequestBody ItemRequestDto itemDto) {
        itemFacade.deleteItem(itemDto);
        return createResponse(OK, "상품이 삭제되었습니다.");
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, String message) {
        ApiResponse<T> response = ApiResponse.success(status, message, null);
        return ResponseEntity.status(status).body(response);
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(HttpStatus status, T data) {
        ApiResponse<T> response = ApiResponse.success(status, data);
        return ResponseEntity.status(status).body(response);
    }

}
