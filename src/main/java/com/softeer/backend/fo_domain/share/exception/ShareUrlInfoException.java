package com.softeer.backend.fo_domain.share.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.exception.GeneralException;

public class ShareUrlInfoException extends GeneralException {
    public ShareUrlInfoException(BaseErrorCode code) {
        super(code);
    }
}
