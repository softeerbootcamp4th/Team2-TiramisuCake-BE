package com.softeer.backend.fo_domain.comment.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.exception.GeneralException;

/**
 * 기대평 관련 예외 클래스
 */
public class CommentException extends GeneralException {

    public CommentException(BaseErrorCode code) {
        super(code);
    }
}
