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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<ItemImage> itemImages = savedItemInfo.getItemImages();

        return new ItemResponseDto(savedItemInfo.getItem(), getImageUrls(itemImages));
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

        processImageUpdates(updatedItem, addImages, deleteImageSeq, updateImageSeq, changeImages);
        List<String> imageUrls = getImageUrls(itemImageService.findActivateImageByItem(updatedItem));

        return new ItemResponseDto(updatedItem, imageUrls);
    }

    private void processImageUpdates(Item item,
                                     List<MultipartFile> addImages,
                                     List<Integer> deleteImageSeq,
                                     List<Integer> updateImageSeq,
                                     List<MultipartFile> changeImages) {
        Optional.ofNullable(addImages)
                .filter(list -> !list.isEmpty())
                .ifPresent(images -> itemImageService.addItemImage(images, item));

        Optional.ofNullable(deleteImageSeq)
                .filter(list -> !list.isEmpty())
                .ifPresent(seq -> itemImageService.deleteItemImage(seq, item));

        Optional.ofNullable(updateImageSeq)
                .filter(list -> !list.isEmpty())
                .ifPresent(seq -> itemImageService.updateSeq(seq, item));

        Optional.ofNullable(changeImages)
                .filter(list -> !list.isEmpty())
                .ifPresent(images -> itemImageService.changeImages(images, item));
    }

    public List<ItemListDto> getAllItems(ItemSearch itemSearch) {
        List<Item> itemList = itemService.findAll(itemSearch);
        return itemList.stream()
                .map(item -> new ItemListDto(item, getImageUrls(itemImageService.findActivateImageByItem(item))))
                .toList();
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
        return itemImages.stream()
                .map(ItemImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
