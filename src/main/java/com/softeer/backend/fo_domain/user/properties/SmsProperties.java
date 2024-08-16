package com.softeer.backend.fo_domain.user.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

/**
 * CoolSms Api 속성 관리 클래스
 */
@Getter
@ConfigurationProperties("coolsms.api")
public class SmsProperties {
    private final String key;
    private final String secret;
    private final String senderNumber;
    private final String url;

    @ConstructorBinding
    public SmsProperties(String key, String secret, String senderNumber, String url) {
        this.key = key;
        this.secret = secret;
        this.senderNumber = senderNumber;
        this.url = url;
    }
}
