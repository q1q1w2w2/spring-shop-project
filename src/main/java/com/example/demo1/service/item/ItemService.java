package com.example.demo1.service.item;

import com.example.demo1.dto.item.SaveItemResponseDto;
import com.example.demo1.entity.item.Category;
import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.item.ItemImage;
import com.example.demo1.entity.user.User;
import com.example.demo1.dto.item.ItemDto;
import com.example.demo1.dto.item.ItemUpdateDto;
import com.example.demo1.dto.order.ItemSearch;
import com.example.demo1.exception.Item.category.CategoryNotFoundException;
import com.example.demo1.exception.Item.item.ItemAlreadyDeleteException;
import com.example.demo1.exception.Item.item.ItemNotFoundException;
import com.example.demo1.exception.Item.item.ItemOwnershipException;
import com.example.demo1.repository.item.CategoryRepository;
import com.example.demo1.repository.item.ItemImageRepository;
import com.example.demo1.repository.item.ItemRepository;
import com.example.demo1.util.constant.ItemStatus;
import com.example.demo1.util.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.demo1.util.constant.Constants.*;
import static com.example.demo1.util.constant.ItemStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final S3Service s3Service;
    private final ItemImageRepository itemImageRepository;

    @Transactional
    public SaveItemResponseDto save(ItemDto dto, List<MultipartFile> images, User user) {
        if (dto.getPrice() < 0) {
            throw new IllegalArgumentException("상품 가격은 양수여야 합니다.");
        }

        Item item = Item.builder()
                .itemName(dto.getItemName())
                .userIdx(user)
                .price(dto.getPrice())
                .category(getCategory(dto.getCategory()))
                .explanation(dto.getExplanation())
                .build();
        itemRepository.save(item);

        if (images != null) {
            List<String> imageUrls = s3Service.saveFiles(images);
            for (int i = 0; i < imageUrls.size(); i++) {
                String objectKey = s3Service.getObjectKey(imageUrls.get(i));
                ItemImage itemImage = ItemImage.builder()
                        .item(item)
                        .imageUrl(objectKey)
                        .seq(i + 1)
                        .build();
                itemImageRepository.save(itemImage);
            }
        }

        return new SaveItemResponseDto(item, itemImageRepository.findByItem(item));
    }

    @Transactional
    public Item update(ItemUpdateDto dto, User user) {
        Item findItem = findByIdx(dto.getIdx());
        checkUserOwnership(user, findItem);

        Item item = Item.builder()
                .itemName(dto.getItemName() != null ? dto.getItemName() : findItem.getItemName())
                .category(dto.getCategory() != null ? getCategory(dto.getCategory()) : findItem.getCategory())
                .price(dto.getPrice() != null ? dto.getPrice() : findItem.getPrice())
                .explanation(dto.getExplanation() != null ? dto.getExplanation() : findItem.getExplanation())
                .build();

        return findItem.update(item);
    }

    @Transactional
    public void delete(Long itemIdx, User user) {
        Item item = findByIdx(itemIdx);
        if (item.getStatus() == DELETED.getValue()) {
            log.error("상품 삭제 실패: 이미 삭제된 상품");
            throw new ItemAlreadyDeleteException("이미 삭제된 상품입니다.");
        }
        checkUserOwnership(user, item);
        item.delete();
        log.info("상품 삭제 성공");
    }

    private Category getCategory(Long dto) {
        return categoryRepository.findById(dto)
                .orElseThrow(CategoryNotFoundException::new);
    }

    private void checkUserOwnership(User user, Item item) {
        if (!item.getUser().equals(user)) {
            throw new ItemOwnershipException();
        }
    }

    public Item findByIdx(Long itemIdx) {
        return itemRepository.findById(itemIdx)
                .orElseThrow(ItemNotFoundException::new);
    }

    public List<Item> findAll(ItemSearch itemSearch) {
        return itemRepository.findAll(itemSearch);
    }

}
