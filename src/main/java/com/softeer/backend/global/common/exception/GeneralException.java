package com.softeer.backend.global.common.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 커스텀 예외의 최고 조상 클래스
 */
@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private final BaseErrorCode code;

    /**
     * Error 정보를 갖고있는 ErrorReasonDto를 반환하는 메서드
     *
     * @return ErrorReasonDto 객체
     */
    public ResponseDto.ErrorReasonDto getErrorReason() {
        return this.code.getReason();
    }

}
