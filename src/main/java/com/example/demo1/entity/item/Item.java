package com.example.demo1.entity.item;

import com.example.demo1.entity.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @Column(name = "item_name")
    private String itemName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    private Category category;

    @Column(name = "price")
    private Integer price;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "state")
    private int state; // 0: 활성, 1: 삭제

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemImage> images = new ArrayList<>();

    @Builder
    public Item(User userIdx, String itemName, Category category, int price, String explanation, int state, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = userIdx;
        this.itemName = itemName;
        this.category = category;
        this.price = price;
        this.explanation = explanation;
        this.state = state;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Item update(Item item) {
        this.itemName = item.getItemName();
        this.category = item.getCategory();
        this.price = item.getPrice();
        this.explanation = item.getExplanation();
        this.updatedAt = LocalDateTime.now().withNano(0);
        return this;
    }

    public void delete() {
        this.state = 1;
        this.updatedAt = LocalDateTime.now().withNano(0);
    }
}
