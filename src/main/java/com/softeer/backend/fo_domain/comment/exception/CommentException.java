package com.softeer.backend.fo_domain.comment.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.exception.GeneralException;

public class CommentException extends GeneralException {

    public CommentException(BaseErrorCode code) {
        super(code);
    }
}
