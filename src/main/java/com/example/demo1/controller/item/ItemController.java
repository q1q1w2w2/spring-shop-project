package com.example.demo1.controller.item;

import com.example.demo1.dto.common.ApiResponse;
import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.item.ItemImage;
import com.example.demo1.entity.user.User;
import com.example.demo1.dto.item.*;
import com.example.demo1.dto.order.ItemSearch;
import com.example.demo1.service.item.ItemImageService;
import com.example.demo1.service.item.ItemService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemImageService itemImageService;

    @PostMapping("/api/item/save")
    public ResponseEntity<ApiResponse<ItemResponseDto>> saveItem(
            @Validated @RequestPart(value = "item") ItemDto itemDto,
            @RequestPart(value = "image", required = false) List<MultipartFile> images
    ) {
        User user = userService.getCurrentUser();

        Map<String, Object> savedItemInfo = itemService.save(itemDto, images, user);
        Item item = (Item) savedItemInfo.get("item");
        List<ItemImage> itemImages = (List<ItemImage>) savedItemInfo.get("itemImages");
        ArrayList<String> imageUrls = getImageUrls(itemImages);

        ApiResponse<ItemResponseDto> response = ApiResponse.success(OK, new ItemResponseDto(item, imageUrls));
        return ResponseEntity.ok(response);
    }

    private ArrayList<String> getImageUrls(List<ItemImage> itemImages) {
        ArrayList<String> imageUrls = new ArrayList<>();
        for (ItemImage itemImage : itemImages) {
            imageUrls.add(itemImage.getImageUrl());
        }
        return imageUrls;
    }

    @PatchMapping("/api/item/update")
    public ResponseEntity<ApiResponse<ItemResponseDto>> updateItem(
            @Validated @RequestPart ItemUpdateDto itemUpdateDto,
            @RequestPart(value = "addImage", required = false) List<MultipartFile> addImage,
            @RequestPart(value = "deleteImageSeq", required = false) List<Integer> deleteImageSeq,
            @RequestPart(value = "updateImageSeq", required = false) List<Integer> updateImageSeq,
            @RequestPart(value = "changeImage", required = false) List<MultipartFile> changeImage
    ) {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());

        Item updateItem = itemService.update(itemUpdateDto, user);

        if (addImage != null) {
            itemImageService.addItemImage(addImage, updateItem);
        }
        if (deleteImageSeq != null) {
            itemImageService.deleteItemImage(deleteImageSeq, updateItem);
        }
        if (updateImageSeq != null) {
            itemImageService.updateSeq(updateImageSeq, updateItem);
        }
        if (changeImage != null) {
            itemImageService.changeImages(changeImage, updateItem);
        }

        List<ItemImage> itemImages = itemImageService.findActivateImageByItem(updateItem);
        ArrayList<String> imageUrls = getImageUrls(itemImages);

        ApiResponse<ItemResponseDto> response = ApiResponse.success(OK, new ItemResponseDto(updateItem, imageUrls));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/item/list")
    public ResponseEntity<ApiResponse<List<ItemListDto>>> getAllItems(@ModelAttribute ItemSearch itemSearch) {
        List<Item> itemList = itemService.findAll(itemSearch);
        List<ItemListDto> itemListDto = new ArrayList<>();

        for (Item item : itemList) {
            List<ItemImage> images = itemImageService.findActivateImageByItem(item);
            ArrayList<String> imageUrls = getImageUrls(images);
            itemListDto.add(new ItemListDto(item, imageUrls));
        }

        ApiResponse<List<ItemListDto>> response = ApiResponse.success(OK, itemListDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/item")
    public ResponseEntity<ApiResponse<ItemResponseDto>> itemDetail(@RequestParam Long itemIdx) {
        Item item = itemService.findByIdx(itemIdx);
        List<ItemImage> itemImages = itemImageService.findActivateImageByItem(item);
        ArrayList<String> imageUrls = getImageUrls(itemImages);

        ApiResponse<ItemResponseDto> response = ApiResponse.success(OK, new ItemResponseDto(item, imageUrls));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/api/item/delete")
    public ResponseEntity<ApiResponse<Object>> deleteItem(@RequestBody ItemRequestDto dto) {
        User user = userService.getCurrentUser();
        itemService.delete(dto.getItemIdx(), user);

        ApiResponse<Object> response = ApiResponse.success(OK, "상품이 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

}
