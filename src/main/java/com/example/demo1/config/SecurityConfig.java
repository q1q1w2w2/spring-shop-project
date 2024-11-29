package com.example.demo1.config;

import com.example.demo1.util.jwt.CustomAccessDeniedHandler;
import com.example.demo1.util.jwt.CustomAuthenticationEntryPoint;
import com.example.demo1.util.jwt.JwtFilter;
import com.example.demo1.util.jwt.TokenProvider;
import com.example.demo1.util.oauth.CustomAuthenticationFailureHandler;
import com.example.demo1.util.oauth.CustomAuthenticationSuccessHandler;
import com.example.demo1.service.login.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // spring security 사용
// prePostEnabled: 메서드에 대한 보안 검사(기본값 true)
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@CrossOrigin(origins = "http://localhost:8081", exposedHeaders = "Authorization")
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 를 직접 사용하기 위해 빈으로 등록(authenticate 메서드를 직접 호출하기 위함)
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // CORS(Cross-Origin Resource Sharing), 교차 출처 리소스 곻유
                // 서로 다른 출처일 때의 리소스 요청, 응답을 허용하는 정책
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                .authorizeHttpRequests(authorizeRequest ->
                        authorizeRequest
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/", "/login", "/join").permitAll()
                                .requestMatchers("/api/login", "/api/join").permitAll()
                                .requestMatchers("/item/list", "/item", "/api/category", "/item/review").permitAll()
                                .requestMatchers("/api/mailSend", "/api/mailCode/valid", "/api/user/sendMail", "/api/pwd-reset").permitAll()
                                .anyRequest().authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )

                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // jwt 예외 처리
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                )

                .oauth2Login(oauth2 -> oauth2
                        .successHandler(myAuthenticationSuccessHandler)
                        .failureHandler(myAuthenticationFailureHandler)
                )
        ;

        return http.build();
    }

    // cors
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.setAllowedOrigins(List.of("http://localhost:8081"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE"));
        config.addExposedHeader(JwtFilter.AUTHORIZATION_HEADER);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/authority/**", config);
        return source;
    }

    // 이 부분에 대해서는 spring security의 필터 체인 자체를 생략(jwtFilter 생략)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web ->
                web.ignoring().requestMatchers("/error", "/favicon.ico"));
    }

}
