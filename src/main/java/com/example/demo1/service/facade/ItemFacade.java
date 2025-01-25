package com.example.demo1.service.facade;

import com.example.demo1.dto.item.*;
import com.example.demo1.dto.order.ItemSearch;
import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.item.ItemImage;
import com.example.demo1.entity.user.User;
import com.example.demo1.service.item.ItemImageService;
import com.example.demo1.service.item.ItemService;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemFacade {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemImageService itemImageService;

    @Transactional
    public ItemResponseDto saveItem(ItemDto itemDto, List<MultipartFile> images) {
        SaveItemResponseDto savedItemInfo = itemService.save(itemDto, images, getCurrentUser());

        Item item = savedItemInfo.getItem();
        List<ItemImage> itemImages = savedItemInfo.getItemImages();
        List<String> imageUrls = getImageUrls(itemImages);

        return new ItemResponseDto(item, imageUrls);
    }

    @Transactional
    public ItemResponseDto update(ItemUpdateDto updateDto,
                       List<MultipartFile> addImages,
                       List<Integer> deleteImageSeq,
                       List<Integer> updateImageSeq,
                       List<MultipartFile> changeImages)
    {
        User user = getCurrentUser();
        Item updatedItem = itemService.update(updateDto, user);

        if (addImages != null) {
            itemImageService.addItemImage(addImages, updatedItem);
        }
        if (deleteImageSeq != null) {
            itemImageService.deleteItemImage(deleteImageSeq, updatedItem);
        }
        if (updateImageSeq != null) {
            itemImageService.updateSeq(updateImageSeq, updatedItem);
        }
        if (changeImages != null) {
            itemImageService.changeImages(changeImages, updatedItem);
        }

        List<ItemImage> itemImages = itemImageService.findActivateImageByItem(updatedItem);
        List<String> imageUrls = getImageUrls(itemImages);

        return new ItemResponseDto(updatedItem, imageUrls);
    }

    public List<ItemListDto> getAllItems(ItemSearch itemSearch) {
        List<Item> itemList = itemService.findAll(itemSearch);
        List<ItemListDto> itemDtoList = new ArrayList<>();

        for (Item item : itemList) {
            List<ItemImage> images = itemImageService.findActivateImageByItem(item);
            List<String> imageUrls = getImageUrls(images);
            itemDtoList.add(new ItemListDto(item, imageUrls));
        }

        return itemDtoList;
    }

    public ItemResponseDto getItem(Long itemIdx) {
        Item item = itemService.findByIdx(itemIdx);
        List<ItemImage> itemImages = itemImageService.findActivateImageByItem(item);
        List<String> imageUrls = getImageUrls(itemImages);

        return new ItemResponseDto(item, imageUrls);
    }

    @Transactional
    public void deleteItem(ItemRequestDto itemRequestDto) {
        itemService.delete(itemRequestDto.getItemIdx(), getCurrentUser());
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }

    private List<String> getImageUrls(List<ItemImage> itemImages) {
        List<String> imageUrls = new ArrayList<>();
        for (ItemImage itemImage : itemImages) {
            imageUrls.add(itemImage.getImageUrl());
        }
        return imageUrls;
    }
}
