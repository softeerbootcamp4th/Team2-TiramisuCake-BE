package com.softeer.backend.global.config.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

/**
 * Redis 속성 관리 클래스
 * <p>
 * host: Redis host 정보
 * port: Redis 포트 정보
 */
@Getter
@ConfigurationProperties("spring.data.redis")
public class RedisProperties {
    private final String host;
    private final Integer port;

    @ConstructorBinding
    public RedisProperties(String host, Integer port) {
        this.host = host;
        this.port = port;
    }
}
