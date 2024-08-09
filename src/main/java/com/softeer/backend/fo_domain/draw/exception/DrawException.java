package com.softeer.backend.fo_domain.draw.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.exception.GeneralException;

public class DrawException extends GeneralException {

    public DrawException(BaseErrorCode code) {
        super(code);
    }
}
