package com.softeer.backend.global.config.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeer.backend.global.annotation.argumentresolver.AuthInfoArgumentResolver;
import com.softeer.backend.global.config.properties.JwtProperties;
import com.softeer.backend.global.filter.ExceptionHandlingFilter;
import com.softeer.backend.global.filter.JwtAuthenticationFilter;
import com.softeer.backend.global.filter.JwtAuthorizationFilter;
import com.softeer.backend.global.util.JwtUtil;
import com.softeer.backend.global.util.StringRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMvc 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final StringRedisUtil stringRedisUtil;
    private final JwtProperties jwtProperties;

    /**
     * AuthInfo 애노테이션에 대한 Argument Resolver 등록
     *
     * @param resolvers
     */
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthInfoArgumentResolver());
    }

    /**
     * CORS 설정 메서드
     *
     * @param registry Cors 등록 객체
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins("https://softeer.site", "http://localhost:5173", "https://softeer.shop") // 허용할 도메인 설정
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드 설정
                .allowedHeaders("Content-Type", "Authorization", "Authorization-Refresh") // 허용할 헤더 설정
                .exposedHeaders("Authorization", "Authorization-Refresh") // 클라이언트에 노출할 헤더 설정
                .allowCredentials(true) // 자격 증명 허용
                .maxAge(3600); // preflight 요청의 캐시 시간 설정 (초 단위)
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
        registrationBean.setFilter(new JwtAuthenticationFilter(jwtUtil, stringRedisUtil, jwtProperties));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    /**
     * JwtAuthorizationFilter를 필터에 등록
     */
    @Bean
    public FilterRegistrationBean<JwtAuthorizationFilter> jwtAuthorizationFilter() {
        FilterRegistrationBean<JwtAuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthorizationFilter());
        registrationBean.addUrlPatterns("/admin/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }

}
