package com.softeer.backend.global.config.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

/**
 * JWT 속성 관리 클래스
 * <p>
 * bearer: JWT 토큰 타입
 * secret: JWT 비밀 키
 * accessHeader: Access Token 헤더 이름
 * refreshHeader: Refresh Token 헤더 이름
 * accessExpiration: Access Token 유효 기간
 * refreshExpiration: Refresh Token 유효 기간
 */
@Getter
@ConfigurationProperties("jwt")
public class JwtProperties {
    private final String bearer;
    private final String secret;
    private final String accessHeader;
    private final String refreshHeader;
    private final Long accessExpiration;
    private final Long refreshExpiration;

    @ConstructorBinding
    public JwtProperties(String bearer, String secret, String accessHeader, String refreshHeader,
                         Long accessExpiration, Long refreshExpiration) {
        this.bearer = bearer;
        this.secret = secret;
        this.accessHeader = accessHeader;
        this.refreshHeader = refreshHeader;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }
}