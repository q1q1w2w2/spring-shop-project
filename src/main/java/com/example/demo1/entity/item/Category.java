package com.example.demo1.entity.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "item_category")
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now().withNano(0);
    }
}
