package com.example.demo1.repository.item;

import com.example.demo1.domain.item.Item;
import com.example.demo1.domain.item.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {

    List<ItemImage> findByItem(Item item);

    List<ItemImage> findByItemAndSeqNotOrderBySeq(Item item, int seq);

    ItemImage findByItemAndSeq(Item item, int seq);
}
