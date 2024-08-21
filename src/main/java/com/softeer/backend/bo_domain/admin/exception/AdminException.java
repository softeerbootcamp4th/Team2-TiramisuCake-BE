package com.softeer.backend.bo_domain.admin.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.exception.GeneralException;

/**
 * 어드민 기능 관련 예외 클래스
 */
public class AdminException extends GeneralException {

    public AdminException(BaseErrorCode code) {
        super(code);
    }
}
