package com.softeer.backend.global.config.nurigo;

import com.softeer.backend.fo_domain.user.properties.SmsProperties;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;

@Configuration
@RequiredArgsConstructor
public class DefaultMessageServiceConfig {

    private final SmsProperties smsProperties;

    @Bean
    public DefaultMessageService defaultMessageService() {
        return NurigoApp.INSTANCE.initialize(
                smsProperties.getKey(), smsProperties.getSecret(), smsProperties.getUrl());
    }
}