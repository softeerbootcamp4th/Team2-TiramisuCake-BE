package com.softeer.backend.global.common.exception;


import com.softeer.backend.global.common.code.BaseErrorCode;

/**
 * JWT 관련 예외 클래스
 */
public class JwtAuthenticationException extends GeneralException {

    public JwtAuthenticationException(BaseErrorCode code){
        super(code);
    }
}
