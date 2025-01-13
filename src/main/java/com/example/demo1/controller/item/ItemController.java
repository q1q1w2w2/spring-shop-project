package com.example.demo1.controller.item;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemImageService itemImageService;

    @PostMapping("/api/item/save")
    public ResponseEntity<ItemResponseDto> saveItem(
            @Validated @RequestPart(value = "item") ItemDto itemDto,
            @RequestPart(value = "image", required = false) List<MultipartFile> images
    ) {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());

        Map<String, Object> saveInfo = itemService.save(itemDto, images, user);
        Item item = (Item) saveInfo.get("item");
        List<ItemImage> itemImages = (List<ItemImage>) saveInfo.get("itemImages");

        ArrayList<String> imageUrls = new ArrayList<>();
        for (ItemImage itemImage : itemImages) {
            imageUrls.add(itemImage.getImageUrl());
        }

        ItemResponseDto response = new ItemResponseDto(item, imageUrls);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/api/item/update")
    public ResponseEntity<ItemResponseDto> updateItem(
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
        ArrayList<String> imageUrlList = new ArrayList<>();
        for (ItemImage itemImage : itemImages) {
            imageUrlList.add(itemImage.getImageUrl());
        }

        ItemResponseDto response = new ItemResponseDto(updateItem, imageUrlList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/item/list")
    public ResponseEntity<List<ItemListDto>> getAllItems(@ModelAttribute ItemSearch itemSearch) {
        List<Item> itemList = itemService.findAll(itemSearch);
        List<ItemListDto> response = new ArrayList<>();

        for (Item item : itemList) {
            List<String> imageUrls = new ArrayList<>();

            List<ItemImage> images = itemImageService.findActivateImageByItem(item);
            for (ItemImage image : images) {
                imageUrls.add(image.getImageUrl());
            }

            response.add(new ItemListDto(item, imageUrls));
        }

        log.info("상품 목록 조회 성공");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/item")
    public ResponseEntity<ItemResponseDto> itemDetail(@RequestParam Long itemIdx) {
        Item item = itemService.findByIdx(itemIdx);
        List<ItemImage> itemImages = itemImageService.findActivateImageByItem(item);

        List<String> imageUrls = new ArrayList<>();
        for (ItemImage itemImage : itemImages) {
            imageUrls.add(itemImage.getImageUrl());
        }

        ItemResponseDto response = new ItemResponseDto(item, imageUrls);
        log.info("상품 상세 조회 성공");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/api/item/delete")
    public ResponseEntity deleteItem(@RequestBody ItemRequestDto dto) {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
        itemService.delete(dto.getItemIdx(), user);
        return ResponseEntity.ok(Map.of("message", "상품이 삭제되었습니다."));
    }

}
