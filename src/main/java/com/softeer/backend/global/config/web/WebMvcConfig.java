package com.softeer.backend.global.config.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeer.backend.global.config.properties.JwtProperties;
import com.softeer.backend.global.filter.ExceptionHandlingFilter;
import com.softeer.backend.global.filter.JwtAuthenticationFilter;
import com.softeer.backend.global.util.JwtUtil;
import com.softeer.backend.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final JwtProperties jwtProperties;

    /**
     * CORS 설정 메서드
     *
     * @param registry Cors 등록 객체
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // TODO: Origin 도메인 수정 및 헤더값 설정
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5000")
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE");
    }

    /**
     * ExceptionHandlingFilter를 필터에 등록
     */
    @Bean
    public FilterRegistrationBean<ExceptionHandlingFilter> exceptionHandleFilter() {
        FilterRegistrationBean<ExceptionHandlingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ExceptionHandlingFilter(objectMapper));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    /**
     * JwtAuthenticationFilter를 필터에 등록
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthenticationFilter(jwtUtil, redisUtil, jwtProperties));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

}
