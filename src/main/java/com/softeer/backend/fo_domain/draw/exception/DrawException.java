package com.softeer.backend.fo_domain.draw.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.exception.GeneralException;

/**
 * 추첨 이벤트 예외 발생 시 예외를 처리하는 클래스
 */
public class DrawException extends GeneralException {
    public DrawException(BaseErrorCode code) {
        super(code);
    }
}
