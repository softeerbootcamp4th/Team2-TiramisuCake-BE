package com.softeer.backend.global.util;

import java.security.SecureRandom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RandomCodeUtil {
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CHARACTERS_LENGTH = CHARACTERS.length();
    private static final SecureRandom random = new SecureRandom();

    public String generateRandomCode(int codeLength) {
        StringBuilder code = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS_LENGTH)));
        }

        return code.toString();
    }
}
