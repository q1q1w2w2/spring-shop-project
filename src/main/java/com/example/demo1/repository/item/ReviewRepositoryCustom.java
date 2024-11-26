package com.example.demo1.repository.item;

import com.example.demo1.domain.item.Item;
import com.example.demo1.domain.item.Review;
import com.example.demo1.dto.order.ReviewSearch;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<Review> findAll(ReviewSearch reviewSearch);
    List<Review> findAllByItem(Item item);
}
