package com.softeer.backend.fo_domain.user.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.exception.GeneralException;

/**
 * 사용자와 관련된 예외를 처리하는 클래스
 */
public class UserException extends GeneralException {
    public UserException(BaseErrorCode code) {
        super(code);
    }
}
