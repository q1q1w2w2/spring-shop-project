package com.example.demo1.util.aes;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Slf4j
public class AesUtil {

    public static final String ALGORITHM = "AES";

    // 비밀 키 생성
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(128); // 생성할 키의 길이 128 비트
        return keyGenerator.generateKey(); // 실제 키 생성 SecretKey
    }

    public static SecretKey generateKeyFromString(String keyString) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(keyString.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, 0, 16, ALGORITHM);
    }

    // 문자열 암호화
    public static String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM); // AES를 사용하는 Chiper 객체 생성
        cipher.init(Cipher.ENCRYPT_MODE, key); // 암호화 모드로 초기화
        byte[] encryptedBytes = cipher.doFinal(data.getBytes()); // 암호화
        return Base64.getEncoder().encodeToString(encryptedBytes); // 암호화된 바이트 배열을 Base64로 인코딩하여 문자열로 변환
    }

    // 문자열 복호화
    public static String decrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(decryptedBytes);
    }
}
