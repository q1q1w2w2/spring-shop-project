package com.example.demo1.repository.item;

import com.example.demo1.entity.item.Item;
import com.example.demo1.dto.order.ItemSearch;

import java.util.List;

public interface ItemRepositoryCustom {
    List<Item> findAll(ItemSearch itemSearch);
}
