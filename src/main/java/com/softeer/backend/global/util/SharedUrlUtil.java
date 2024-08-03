package com.softeer.backend.global.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class SharedUrlUtil {
    public String generateSHA256Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes());
        return bytesToHex(hash);
    }

    // byte 배열을 16진수 문자열로 변환
    public String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String generateShortURL(Integer userId) throws NoSuchAlgorithmException {
        String hash = generateSHA256Hash(userId.toString());
        // 해시 값의 처음 16자를 사용하여 단축 URL 생성
        return hash.substring(0, 16);
    }
}
