package com.softeer.backend.global.common.code.status;

import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


/**
 * 에러 응답 코드를 관리하는 Enum 클래스
 */
@Getter
@RequiredArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // Common Error & Global Error
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "G400", "잘못된 요청입니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "G401", "해당 요청에 대한 권한이 없습니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "G402", "데이터를 찾지 못했습니다."),
    _METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "G403", "지원하지 않는 Http Method 입니다."),
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G500", "서버 에러가 발생했습니다."),

    // Validation Error
    _VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "V400", "요청 필드에 대한 검증 예외가 발생했습니다."),

    // JWT Error
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "J400", "인증 과정에서 예외가 발생했습니다. JWT Token 재발급이 필요합니다."),
    _REISSUE_ERROR(HttpStatus.UNAUTHORIZED, "J401", "JWT Token 재발급에서 예외가 발생했습니다. 로그인 요청이 필요합니다."),

    // User & Auth Error
    _AUTH_CODE_NOT_EXIST(HttpStatus.BAD_REQUEST, "A400", "인증 코드 제한시간이 초과되었습니다. 인증 코드 발급 API를 호출하세요."),
    _AUTH_CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "A401", "인증 코드가 일치하지 않습니다."),
    _AUTH_CODE_ATTEMPTS_EXCEEDED(HttpStatus.BAD_REQUEST, "A402",
            "인증 코드의 인증 횟수를 초과하였습니다. 인증 코드 발급 API를 호출하세요."),
    _AUTH_CODE_ISSUE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "A403",
            "인증 코드 발급 횟수를 초과하였습니다. 나중에 다시 시도하세요."),
    _AUTH_CODE_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "A404", "인증되지 않은 상태에서 로그인 할 수 없습니다.");

    // 예외의 Http 상태값
    private final HttpStatus httpStatus;

    // 예외의 커스텀 코드값
    private final String code;

    // 예외 메시지
    private final String message;

    /**
     * Error 정보를 갖고있는 ErrorReasonDto를 반환하는 메서드
     *
     * @return ErrorReasonDto 객체
     */
    @Override
    public ResponseDto.ErrorReasonDto getReason() {
        return ResponseDto.ErrorReasonDto.builder()
                .httpStatus(this.httpStatus)
                .isSuccess(false)
                .code(this.code)
                .message(this.message)
                .build();
    }

    /**
     * HttpStatus를 반환하는 메서드
     *
     * @return HttpStatus 객체
     */
    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    /**
     * 예외 코드를 반환하는 메서드
     *
     * @return 커스텀 코드값
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * 예외 메시지를 반환하는 메서드
     *
     * @return 예외 메시지
     */
    @Override
    public String getErrorMsg() {
        return message;
    }
}
