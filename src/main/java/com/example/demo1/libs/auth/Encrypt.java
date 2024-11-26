package com.example.demo1.libs.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class Encrypt {

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(13);
    private final EncryptProperties encryptProperties;


    // ⚠️ 스프링 시큐리티 사용하지 않고 암호화?
    // 비밀번호 암호화 ( 단방향 )
    public String cryptoPassWord(String passWord) {
        return encoder.encode(passWord);
    }

    // 비밀번호 검증
    public Boolean checkPassWord(String passWord, String hashedPassWord) {
        return encoder.matches(passWord, hashedPassWord);
    }


    private final String ALGORITHM = "AES";  // AES 알고리즘을 사용
    private final String TRANSFORMATION = "AES/CBC/PKCS5Padding";    // CBC 모드와 패딩 방식 지정

    // 양방향 암호화 ( ⚠️ 토큰 값이 너무 길어지는 문제 발생 )
    public String cryptoSync(String serializationString){
        try{
            log.info("serializationString: {}", serializationString);
            final String SECRETKEY = generate16Bytes(encryptProperties.getSecretKey());
            final String INITIV = generate16Bytes(encryptProperties.getSalt());

            // secretKeySpec 를 이용해 비밀키를 생성한다.
            // ⚠️ AES 128 암호화에서는 비밀키와 IV 가 반드시 16바이트(128비트) 이여야함 ( 길이가 16 )
            SecretKeySpec secretKey = new SecretKeySpec(SECRETKEY.getBytes(), ALGORITHM);

            // 초기화 백터(IV) 생성
            // ⚠️ AES 128 암호화에서는 비밀키와 IV 가 반드시 16바이트(128비트) 이여야함 ( 길이가 16 )
            IvParameterSpec iv = new IvParameterSpec(INITIV.getBytes());

            // Cipher 인스턴스 생성 ( AES 알고리즘, CBC 모드, PKCS5Padding 패딩 방식 )
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);    // 암호화 모드로 초기화

            // 입력 문자열을 암호화 후, BASE64 로 인코딩하여 반환
            byte[] encryptArray = cipher.doFinal(serializationString.getBytes());
            return Base64.getEncoder().encodeToString(encryptArray);
        } catch (Exception e){
            // 암호화 과정에서 오류 발생
            throw new RuntimeException("Encryption Error");
        }
    }

    // 랜덤 길이의 문자열을 16 바이트 해시로 생성한다.
    private String generate16Bytes(String input){
        try{
            // SHA-256 알고리즘 사용
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            return (Arrays.toString(hash)).substring(0,16);
        } catch (Exception e){
            throw new IllegalArgumentException("해시 생성 중 오류 발생", e);
        }
    }

    // 양방항 복호화
    public String decryptoSync(String encryptString){
        try{
            final String SECRETKEY = generate16Bytes(encryptProperties.getSecretKey());
            final String INITIV = generate16Bytes(encryptProperties.getSalt());
            // secretKeySpec 를 이용해 비밀키를 생성한다.
            // ⚠️ AES 128 암호화에서는 비밀키와 IV 가 반드시 16바이트(128비트) 이여야함 ( 길이가 16 )
            SecretKeySpec secretKey = new SecretKeySpec(SECRETKEY.getBytes(), ALGORITHM);

            // 초기화 백터(IV) 생성
            // ⚠️ AES 128 암호화에서는 비밀키와 IV 가 반드시 16바이트(128비트) 이여야함 ( 길이가 16 )
            IvParameterSpec iv = new IvParameterSpec(INITIV.getBytes());

            // Cipher 인스턴스 생성
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            // 암호화된 문자열을 BASE64 로 디코딩한 후 복호화
            byte[] decryptArray = Base64.getDecoder().decode(encryptString);
            byte[] plainText = cipher.doFinal(decryptArray);

            return new String(plainText);
        } catch(Exception e) {
            throw new IllegalArgumentException("복호화 중 오류 발생", e);
        }
    }


}