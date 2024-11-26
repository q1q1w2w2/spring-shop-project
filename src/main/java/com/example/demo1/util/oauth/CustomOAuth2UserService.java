package com.example.demo1.util.oauth;

import com.example.demo1.domain.user.User;
import com.example.demo1.dto.user.JoinDto;
import com.example.demo1.dto.user.UpdateDto;
import com.example.demo1.repository.user.UserRepository;
import com.example.demo1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    // OAuth 인증 성공 시 정보를 가져와 저장

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("[CustomOAuth2UserService 실행] - loadUser");

        // 기본 OAuth2UserService 객체 생성
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();

        // OAuth2UserService를 사용하여 OAuth2User 정보를 가져온다.
        OAuth2User oAuth2User = service.loadUser(userRequest);
        // OAuth2User 속성
        Map<String, Object> originAttributes = oAuth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        log.info("originAttributes = {}", originAttributes);
        log.info("registrationId = {}", registrationId);

        // OAuth2Attribute: OAuth2User의 attribute를 서비스 유형에 맞게 담아주는 클래스
        OAuth2Attribute attribute = OAuth2Attribute.of(registrationId, originAttributes);

        log.info("attribute = {}", attribute);

        // OAuth2Attribute를 통해 기존 User update or save
        User user = saveOrUpdate(attribute);
        String loginId = user.getLoginId();

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        // Security Context에 저장됨
        return new OAuth2CustomUser(registrationId, originAttributes, authorities, loginId);
    }

    private User saveOrUpdate(OAuth2Attribute attribute) {
        User findUser = userRepository.findByLoginId(attribute.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 업데이트, 추후 일반 update와 로직 분리(password)
        userService.update(findUser, new UpdateDto("password", null, null, null));

        // 새로 생성
        return userService.joinOAuth(JoinDto.builder()
                .username(attribute.getName())
                .loginId(attribute.getEmail())
                .birth(LocalDate.of(2000, 1, 1))
//                .provider(attribute.getProvider())
                .build());
    }
}
