package com.softeer.backend.global.common.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;

/**
 * JWT 인가 예외 클래스
 */
public class JwtAuthorizationException extends GeneralException {

    public JwtAuthorizationException(BaseErrorCode code) {
        super(code);
    }
}
