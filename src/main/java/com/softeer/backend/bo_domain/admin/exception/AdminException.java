package com.softeer.backend.bo_domain.admin.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.exception.GeneralException;

public class AdminException extends GeneralException {

    public AdminException(BaseErrorCode code) {
        super(code);
    }
}
