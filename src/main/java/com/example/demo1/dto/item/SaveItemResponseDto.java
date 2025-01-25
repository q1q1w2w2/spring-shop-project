package com.example.demo1.dto.item;

import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.item.ItemImage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SaveItemResponseDto {
    private Item item;
    private List<ItemImage> itemImages;
}
