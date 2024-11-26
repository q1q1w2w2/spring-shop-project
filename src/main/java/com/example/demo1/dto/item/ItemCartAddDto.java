package com.example.demo1.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemCartAddDto {

    private Long itemIdx;
    private int ea;
}
