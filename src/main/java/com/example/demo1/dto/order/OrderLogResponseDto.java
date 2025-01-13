package com.example.demo1.dto.order;

import com.example.demo1.entity.item.ItemImage;
import com.example.demo1.entity.item.OrderLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class OrderLogResponseDto {

    private Long orderLogIdx;
    private Long itemIdx;
    private String itemName;
    private int ea;
    private List<String> itemImages;
    private int review;


    //상품idx, 상품명, 수량, 이미지, 리뷰
    public OrderLogResponseDto(OrderLog orderLog) {
        this.itemIdx = orderLog.getItem().getIdx();
        this.orderLogIdx = orderLog.getIdx();
        this.ea = orderLog.getEa();
        this.review = orderLog.getReview();
        this.itemName = orderLog.getItem().getItemName();
        this.itemImages = orderLog.getItem().getImages().stream()
                .map(ItemImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
