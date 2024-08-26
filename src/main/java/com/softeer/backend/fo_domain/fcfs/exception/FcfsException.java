package com.softeer.backend.fo_domain.fcfs.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.exception.GeneralException;

/**
 * 선착순 관련 예외 클래스
 */
public class FcfsException extends GeneralException {

    public FcfsException(BaseErrorCode code) {
        super(code);
    }
}
