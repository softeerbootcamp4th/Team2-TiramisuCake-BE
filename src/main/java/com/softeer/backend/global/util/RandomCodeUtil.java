package com.softeer.backend.global.util;

import java.security.SecureRandom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 길이 값을 이용해 랜덤한 코드를 생성하는 클래스
 */
@Component
@RequiredArgsConstructor
public class RandomCodeUtil {
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CHARACTERS_LENGTH = CHARACTERS.length();
    private static final SecureRandom random = new SecureRandom();

    /**
     * 길이를 매개변수로 소문자, 대문자, 숫자를 조합한 랜덤한 코드를 생성한다
     */
    public String generateRandomCode(int codeLength) {
        StringBuilder code = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS_LENGTH)));
        }

        return code.toString();
    }
}
