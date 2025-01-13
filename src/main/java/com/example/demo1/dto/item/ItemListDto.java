package com.example.demo1.dto.item;

import com.example.demo1.entity.item.Item;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemListDto {

    private Long idx;
    private String itemName;
    private String category;
    private String explanation;
    private int price;
    private List<String> imageUrls;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public ItemListDto(Item item, List<String> imageUrls) {
        this.idx = item.getIdx();
        this.itemName = item.getItemName();
        this.category = item.getCategory().getCategoryName();
        this.explanation = item.getExplanation();
        this.price = item.getPrice();
        this.imageUrls = imageUrls;
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
    }
}
