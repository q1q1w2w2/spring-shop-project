package com.example.demo1.util.oauth;

import com.example.demo1.entity.user.User;
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

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("[CustomOAuth2UserService 실행] - loadUser");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();

        OAuth2User oAuth2User = service.loadUser(userRequest);
        Map<String, Object> originAttributes = oAuth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        log.info("originAttributes = {}", originAttributes);
        log.info("registrationId = {}", registrationId);

        OAuth2Attribute attribute = OAuth2Attribute.of(registrationId, originAttributes);

        log.info("attribute = {}", attribute);

        User user = saveOrUpdate(attribute);
        String loginId = user.getLoginId();

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return new OAuth2CustomUser(registrationId, originAttributes, authorities, loginId);
    }

    private User saveOrUpdate(OAuth2Attribute attribute) {
        User findUser = userRepository.findByLoginId(attribute.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        userService.update(findUser, new UpdateDto("password", null, null, null));

        return userService.joinOAuth(JoinDto.builder()
                .username(attribute.getName())
                .loginId(attribute.getEmail())
                .birth(LocalDate.of(2000, 1, 1))
//                .provider(attribute.getProvider())
                .build());
    }
}
