package com.example.demo1.entity.item;

import com.example.demo1.entity.user.User;
import com.example.demo1.util.constant.ItemStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo1.util.constant.ItemStatus.*;

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
    private int status = ACTIVATED.getValue();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemImage> images = new ArrayList<>();

    @Builder
    public Item(User userIdx, String itemName, Category category, int price, String explanation) {
        this.user = userIdx;
        this.itemName = itemName;
        this.category = category;
        this.price = price;
        this.explanation = explanation;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now().withNano(0);
    }

    public Item update(Item item) {
        this.itemName = item.getItemName();
        this.category = item.getCategory();
        this.price = item.getPrice();
        this.explanation = item.getExplanation();
        return this;
    }

    public void delete() {
        this.status = DELETED.getValue();
    }
}
