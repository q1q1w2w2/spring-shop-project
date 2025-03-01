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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.demo1.util.constant.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

        return imageUrls.stream()
                .map(imageUrl -> {
                    String objectKey = s3Service.getObjectKey(imageUrl);
                    ItemImage itemImage = ItemImage.builder()
                            .item(item)
                            .imageUrl(objectKey)
                            .seq(lastImageSeq + imageUrls.indexOf(imageUrl) + 1)
                            .build();
                    return itemImageRepository.save(itemImage).getImageUrl();
                })
                .toList();
    }

    @Transactional
    public void deleteItemImage(List<Integer> deleteImageSeq, Item item) {
        Set<Integer> deleteSet = new HashSet<>(deleteImageSeq);
        List<ItemImage> itemImages = itemImageRepository.findByItemAndSeqNotOrderBySeq(item, DELETED_IMAGE);

        Set<Integer> existSeq = itemImages.stream()
                .map(ItemImage::getSeq)
                .collect(Collectors.toSet());

        deleteSet.stream()
                .filter(seq -> !existSeq.contains(seq))
                .findAny()
                .ifPresent(seq -> {
                    throw new InvalidImageSequenceException(seq + "번 seq는 존재하지 않습니다.");
                });

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
        int size = itemImages.size();

        if (updateImageSeq.size() != size) {
            throw new InvalidImageSequenceException("업데이트할 이미지와 주어진 seq의 수가 일치하지 않습니다.");
        }

        Set<Integer> uniqueSeq = new HashSet<>(updateImageSeq);
        if (uniqueSeq.size() != size) {
            throw new InvalidImageSequenceException("중복된 seq가 존재합니다.");
        }

        updateImageSeq.stream()
                .filter(seq -> seq < 1 || seq > itemImages.size())
                .findAny()
                .ifPresent(seq -> {
                    throw new InvalidImageSequenceException("seq에 들어가는 값은 1과 " + itemImages.size() + "사이의 값이어야 합니다.");
                });

        IntStream.range(0, size)
                .forEach(i -> itemImages.get(i).setSeq(updateImageSeq.get(i)));
    }

    @Transactional
    public List<String> changeImages(List<MultipartFile> images, Item item) {
        List<ItemImage> itemImages = itemImageRepository.findByItemAndSeqNotOrderBySeq(item, DELETED_IMAGE);
        itemImages.forEach(itemImage -> itemImage.setSeq(DELETED_IMAGE));

        return addItemImage(images, item);
    }
}
