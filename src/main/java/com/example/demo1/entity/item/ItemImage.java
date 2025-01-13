package com.example.demo1.entity.item;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_idx")
    private Item item;

    @Column(name = "image_url")
    private String imageUrl;

    @Setter
    @Column(name = "seq")
    private Integer seq; // 이미지 출력 순서

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public ItemImage(Item item, String imageUrl, int seq, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.item = item;
        this.imageUrl = imageUrl;
        this.seq = seq;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void delete() {
        this.seq = 0;
    }

}
