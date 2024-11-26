package com.example.demo1.util.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private Set<String> uploadedFileNames = new HashSet<>();
    private Set<Long> uploadedFileSizes = new HashSet<>();

    // 파일 저장
    public String saveFile(MultipartFile file) {
        String fileName = generateRandomFileName(file);
        log.info("이미지 업로드: {}", fileName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            log.error("이미지 업로드 실패");
            throw new RuntimeException(e);
        }

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 파일 저장(여러 개)
    public List<String> saveFiles(List<MultipartFile> files) {
        List<String> uploadUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (isDuplicate(file)) {
                throw new IllegalArgumentException("중복된 파일입니다.");
            }

            uploadUrls.add(saveFile(file));
        }

        clear();

        return uploadUrls;
    }

    // 파일 삭제
    public void deleteFile(String objectKey) {
        if (!amazonS3Client.doesObjectExist(bucket, objectKey)) {
            throw new IllegalArgumentException("해당 파일이 존재하지 않습니다.");
        }

        try {
            amazonS3Client.deleteObject(bucket, objectKey);
        } catch (Exception e) {
            log.error("이미지 삭제 중 오류 발생 : {}", e.getMessage());
            throw new IllegalArgumentException("이미지 삭제 중 오류 발생");
        }

        log.info("이미지 삭제 완료 : {}", objectKey);
    }

    // UUID 생성
    private String generateRandomFileName(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = validateFileExtension(originalFilename);
        return UUID.randomUUID() + "." + fileExtension;
    }

    // 한 요청에 중복 파일 유무 검사
    private boolean isDuplicate(MultipartFile file) {
        String filename = file.getOriginalFilename();
        Long fileSize = file.getSize();

        if (uploadedFileNames.contains(filename) && uploadedFileSizes.contains(fileSize)) {
            return true;
        }

        uploadedFileNames.add(filename);
        uploadedFileSizes.add(fileSize);

        return false;
    }

    // S3에 저장된 URL의 객체 키(folder/file.jpg)
    public String getObjectKey(String fileUrl) {
        String[] urlParts = fileUrl.split("/");
        String fileBucket = urlParts[2].split("\\.")[0];

        if (!fileBucket.equals(bucket)) {
            throw new IllegalArgumentException("존재하지 않는 버킷입니다.");
        }

        return String.join("/", Arrays.copyOfRange(urlParts, 3, urlParts.length));
    }

    // 확장자 검증 및 추출
    private String validateFileExtension(String originalFilename) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        List<String> allowed = Arrays.asList("jpg", "png", "gif", "jpeg");
        if (!allowed.contains(fileExtension)) {
            throw new IllegalArgumentException("올바르지 않은 파일 확장자입니다.");
        }
        return fileExtension;
    }

    private void clear() {
        uploadedFileNames.clear();
        uploadedFileSizes.clear();
    }
}
