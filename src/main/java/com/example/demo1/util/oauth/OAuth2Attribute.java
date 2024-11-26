package com.example.demo1.util.oauth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Slf4j
public class OAuth2Attribute {
    // OAuth 인증을 통해 얻은 사용자의 정보

    private Map<String, Object> attributes; // 사용자 속성 정보
    private String nameAttributesKey; // 사용자 속성의 키 값
    private String name;
    private String email;
    private String gender;
    private String ageRange;
    private String profileImageUrl;
    private String provider;

    @Builder
    public OAuth2Attribute(Map<String, Object> attributes, String nameAttributesKey, String name, String email, String gender, String ageRange, String profileImageUrl, String provider) {
        this.attributes = attributes;
        this.nameAttributesKey = nameAttributesKey;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.ageRange = ageRange;
        this.profileImageUrl = profileImageUrl;
        this.provider = provider;
    }

    // 서비스에 따라 OAuth2Attribute 객체를 생성하는 메서드
    // 서비스마다 가져오는 요소나 이름이 다르기 때문
    public static OAuth2Attribute of(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return ofGoogle("sub", attributes, provider);
            case "naver":
                return ofNaver("id", attributes, provider);
            case "kakao":
                return ofKakao("id", attributes, provider);
            default:
                throw new RuntimeException();
        }
    }

    private static OAuth2Attribute ofGoogle(String usernameAttributeName, Map<String, Object> attributes, String provider) {
        log.info("[ofGoogle]");
        return OAuth2Attribute.builder()
                .name(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("email")))
                .profileImageUrl(String.valueOf(attributes.get("profile_image_url")))
                .attributes(attributes)
                .provider(provider)
                .nameAttributesKey(usernameAttributeName)
                .build();
    }

    private static OAuth2Attribute ofNaver(String usernameAttributeName, Map<String, Object> attributes, String provider) {
        log.info("[ofNaver]");
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attribute.builder()
                .name(String.valueOf(response.get("name")))
                .email(String.valueOf(response.get("email")))
//                .profileImageUrl(String.valueOf(response.get("profile_image_url")))
//                .ageRange((String) response.get("age"))
//                .gender((String) response.get("gender"))
                .attributes(response)
                .provider(provider)
                .nameAttributesKey(usernameAttributeName)
                .build();
    }

    private static OAuth2Attribute ofKakao(String usernameAttributeName, Map<String, Object> attributes, String provider) {
        log.info("[ofKakao]");
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2Attribute.builder()
                .name(String.valueOf(kakaoProfile.get("nickname")))
                .email(String.valueOf(kakaoAccount.get("email")))
//                .gender(String.valueOf(kakaoAccount.get("gender")))
//                .ageRange(String.valueOf(kakaoAccount.get("age_range")))
                .profileImageUrl(String.valueOf(kakaoProfile.get("profile_image_url")))
                .attributes(attributes)
                .provider(provider)
                .nameAttributesKey(usernameAttributeName)
                .build();
    }
}
