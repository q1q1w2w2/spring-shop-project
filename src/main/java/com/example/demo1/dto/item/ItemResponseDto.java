package com.example.demo1.dto.item;

import com.example.demo1.entity.item.Item;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {

    private Long itemIdx;
    private Long userIdx;
    private String itemName;
    private Long category;
//    private int quantity;
    private int price;
    private String explanation;
    private List<String> imageUrls = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ItemResponseDto(Item item) {
        this.itemIdx = item.getIdx();
        this.userIdx = item.getUser().getId();
        this.itemName = item.getItemName();
        this.category = item.getCategory().getIdx();
//        this.quantity = item.getQuantity();
        this.price = item.getPrice();
        this.explanation = item.getExplanation();
//        this.imageUrls = imageUrls;
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
    }

    public ItemResponseDto(Item item, List<String> imageUrls) {
        this.itemIdx = item.getIdx();
        this.userIdx = item.getUser().getId();
        this.itemName = item.getItemName();
        this.category = item.getCategory().getIdx();
//        this.quantity = item.getQuantity();
        this.price = item.getPrice();
        this.explanation = item.getExplanation();
        this.imageUrls = imageUrls;
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
    }
}
