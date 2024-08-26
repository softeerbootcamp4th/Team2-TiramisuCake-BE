package com.softeer.backend.bo_domain.admin.util;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * 어드민 계정 비밀번호를 암호화하는 클래스
 */
@Component
public class PasswordEncoder {

    /**
     * 비밀번호를 암호화하여 반환하는 메서드
     */
    public String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * 암호화된 비밀번호와 사용자가 입력한 비밀번호가 같은지를 반환하는 메서드
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}