package com.softeer.backend.fo_domain.user.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.embedded.RedisServer;

import java.io.IOException;

@SpringBootTest
public class VerificationServiceTest {

    @Autowired
    private VerificationService verificationService;


    @BeforeEach
    void setUp() throws IOException {
        RedisServer redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @Test
    void sendVerificationCodeTest(){
        verificationService.sendVerificationCode("010-4604-1765");
    }

}
