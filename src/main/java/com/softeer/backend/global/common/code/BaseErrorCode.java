package com.softeer.backend.global.common.code;


import com.softeer.backend.global.common.response.ResponseDto;
import org.springframework.http.HttpStatus;

/**
 * 예외 응답 코드를 관리하는 인터페이스
 */
public interface BaseErrorCode {

    ResponseDto.ErrorReasonDto getReason();

    HttpStatus getHttpStatus();

    String getCode();

    String getErrorMsg();
}
