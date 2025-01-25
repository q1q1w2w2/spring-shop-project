package com.example.demo1.service.login;

import com.example.demo1.entity.user.User;
import com.example.demo1.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String loginId) {
        return userRepository.findByLoginId(loginId)
                .map(user -> createUser(loginId, user))
                .orElseThrow(() -> new UsernameNotFoundException("[" + loginId + "]를 찾을 수 없습니다"));
    }

    private org.springframework.security.core.userdetails.User createUser(String loginId, User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getAuthority());

        return new org.springframework.security.core.userdetails.User(
                loginId, user.getPassword(), Collections.singletonList(authority)
        );
    }
}
