package com.softeer.backend.fo_domain.share.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.exception.GeneralException;

/**
 * 공유 url 예외를 처리하기 위한 클래스
 */
public class ShareUrlInfoException extends GeneralException {
    public ShareUrlInfoException(BaseErrorCode code) {
        super(code);
    }
}
