package com.example.demo1.web.item;

import com.example.demo1.domain.item.Item;
import com.example.demo1.domain.item.ItemImage;
import com.example.demo1.domain.user.User;
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
import org.springframework.ui.Model;
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

    // 상품 저장
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

    // 상품 수정
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

        // ItemImage 추가
        if (addImage != null) {
            itemImageService.addItemImage(addImage, updateItem);
        }
        // ItemImage 삭제(seq를 받아서 삭제하는 방식)
        if (deleteImageSeq != null) {
            itemImageService.deleteItemImage(deleteImageSeq, updateItem);
        }
        // ItemImage 순서(seq) 변경
        // updateImagSeq에는 항상 모든 이미지의 seq가 넘어와야 함
        if (updateImageSeq != null) {
            itemImageService.updateSeq(updateImageSeq, updateItem);
        }
        // 기존 이미지 제거 후 새로운 이미지 등록
        if (changeImage != null) {
            itemImageService.changeImages(changeImage, updateItem);
        }

        // response에 addImageUrls 추가 + 삭제된 url 제거
        List<ItemImage> itemImages = itemImageService.findActivateImageByItem(updateItem);
        ArrayList<String> imageUrlList = new ArrayList<>();
        for (ItemImage itemImage : itemImages) {
            imageUrlList.add(itemImage.getImageUrl());
        }

        ItemResponseDto response = new ItemResponseDto(updateItem, imageUrlList);
        return ResponseEntity.ok(response);
    }

    // 상품 목록 조회
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

    // 상품 상세 조회
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

    // 상품 삭제(soft delete)
    @PatchMapping("/api/item/delete")
    public ResponseEntity deleteItem(@RequestBody ItemRequestDto dto) {
        User user = userService.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
        itemService.delete(dto.getItemIdx(), user);
        return ResponseEntity.ok(Map.of("message", "상품이 삭제되었습니다."));
    }

}
