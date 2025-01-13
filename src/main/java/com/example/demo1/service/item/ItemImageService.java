package com.example.demo1.service.item;

import com.example.demo1.entity.item.Item;
import com.example.demo1.entity.item.ItemImage;
import com.example.demo1.exception.Item.itemImage.InvalidImageSequenceException;
import com.example.demo1.repository.item.ItemImageRepository;
import com.example.demo1.util.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.demo1.util.constant.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemImageService {

    private final ItemImageRepository itemImageRepository;
    private final S3Service s3Service;

    public List<ItemImage> findActivateImageByItem(Item item) {
        return itemImageRepository.findByItemAndSeqNotOrderBySeq(item, DELETED_IMAGE);
    }

    @Transactional
    public List<String> addItemImage(List<MultipartFile> images, Item item) {
        List<String> imageUrls = s3Service.saveFiles(images);
        int lastImageSeq = itemImageRepository.findByItemAndSeqNotOrderBySeq(item, DELETED_IMAGE).size();
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < imageUrls.size(); i++) {
            String objectKey = s3Service.getObjectKey(imageUrls.get(i));

            ItemImage itemImage = ItemImage.builder()
                    .item(item)
                    .imageUrl(objectKey)
                    .seq(lastImageSeq + i + 1)
                    .createdAt(LocalDateTime.now().withNano(0))
                    .updatedAt(LocalDateTime.now().withNano(0))
                    .build();

            ItemImage saveImage = itemImageRepository.save(itemImage);
            list.add(saveImage.getImageUrl());
        }
        return list;
    }

    @Transactional
    public void deleteItemImage(List<Integer> deleteImageSeq, Item item) {
        Set<Integer> deleteSet = new HashSet<>(deleteImageSeq); // 중복방지(set)
        List<ItemImage> itemImages = itemImageRepository.findByItemAndSeqNotOrderBySeq(item, DELETED_IMAGE);

        Set<Integer> existSeq = new HashSet<>();
        for (ItemImage itemImage : itemImages) {
            existSeq.add(itemImage.getSeq());
        }

        for (int seq : deleteSet) {
            if (!existSeq.contains(seq)) {
                throw new InvalidImageSequenceException(seq + "번 seq는 존재하지 않습니다.");
            }
        }

        int newSeq = 1;
        for (ItemImage itemImage : itemImages) {
            if (deleteSet.contains(itemImage.getSeq())) {
                itemImage.delete();
            } else {
                itemImage.setSeq(newSeq++);
            }
        }
    }

    @Transactional
    public void updateSeq(List<Integer> updateImageSeq, Item item) {
        List<ItemImage> itemImages = itemImageRepository.findByItemAndSeqNotOrderBySeq(item, DELETED_IMAGE);

        if (updateImageSeq.size() != itemImages.size()) {
            throw new InvalidImageSequenceException("업데이트할 이미지와 주어진 seq의 수가 일치하지 않습니다.");
        }

        Set<Integer> uniqueSeq = new HashSet<>(updateImageSeq);
        if (uniqueSeq.size() != itemImages.size()) {
            throw new InvalidImageSequenceException("중복된 seq가 존재합니다.");
        }

        for (int seq : updateImageSeq) {
            if (seq < 1 || seq > itemImages.size()) {
                throw new InvalidImageSequenceException("seq에 들어가는 값은 1과 " + itemImages.size() + "사이의 값이어야 합니다.");
            }
        }

        for (int i = 0; i < itemImages.size(); i++) {
            itemImages.get(i).setSeq(updateImageSeq.get(i));
        }
    }

    @Transactional
    public List<String> changeImages(List<MultipartFile> images, Item item) {
        List<ItemImage> itemImages = itemImageRepository.findByItemAndSeqNotOrderBySeq(item, DELETED_IMAGE);
        for (ItemImage itemImage : itemImages) {
            itemImage.setSeq(DELETED_IMAGE);
        }

        return addItemImage(images, item);
    }
}
