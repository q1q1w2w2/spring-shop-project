package com.example.demo1.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSearch {

    private Long itemIdx;
//    private Long categoryIdx;
    private Integer sortByScore; // 0: 오름차순, 1: 내림차순
}
