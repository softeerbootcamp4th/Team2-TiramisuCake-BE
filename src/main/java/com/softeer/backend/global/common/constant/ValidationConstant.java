package com.softeer.backend.global.common.constant;

import lombok.Getter;

public class ValidationConstant {
    public static final String PHONE_NUMBER_REGEX ="^01[016789]\\d{8}$";
    public static final String PHONE_NUMBER_MSG ="잘못된 전화번호 형식입니다.";

    public static final String VERIFICATION_CODE_REGEX = "^[a-zA-Z0-9]{6}$";
    public static final String VERIFICATION_CODE_MSG = "잘못된 인증코드 형식입니다.";

}
