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
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증 과정에서 오류가 발생했습니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "찾지 못했습니다."),
    _METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "지원하지 않는 Http Method 입니다."),
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 에러가 발생했습니다."),
    _METHOD_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "METHOD_ARGUMENT_ERROR",
            "올바르지 않은 클라이언트 요청값입니다."),

    // JWT Error
    _JWT_ACCESS_TOKEN_IS_NOT_EXIST(HttpStatus.UNAUTHORIZED,
            "JWT_ACCESS_TOKEN_IS_NOT_EXIST", "Authorization 헤더에 Access Token 정보가 존재하지 않습니다."),
    _JWT_REFRESH_TOKEN_IS_NOT_EXIST(HttpStatus.UNAUTHORIZED,
            "JWT_REFRESH_TOKEN_IS_NOT_EXIST", "Authorization-Refresh 헤더에 JWT 정보가 존재하지 않습니다."),
    _JWT_ACCESS_TOKEN_IS_NOT_VALID(HttpStatus.UNAUTHORIZED, "JWT_ACCESS_TOKEN_IS_NOT_VALID", "Access Token 이 유효하지 않습니다."),
    _JWT_REFRESH_TOKEN_IS_NOT_VALID(HttpStatus.UNAUTHORIZED, "JWT_REFRESH_TOKEN_IS_NOT_VALID",
            "Refresh Token 이 유효하지 않습니다."),
    _JWT_ACCESS_TOKEN_IS_VALID(HttpStatus.UNAUTHORIZED, "JWT_ACCESS_TOKEN_IS_VALID", "Access Token 이 유효합니다."),
    _JWT_REFRESH_TOKEN_IS_NOT_MATCH(HttpStatus.UNAUTHORIZED, "JWT_REFRESH_TOKEN_IS_NOT_MATCH",
            "Refresh Token 이 일치하지 않습니다."),

    // User & Auth Error
    _USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "유저가 존재하지 않습니다."),
    _ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "해당 요청에 대한 권한이 없습니다."),
    _AUTH_CODE_NOT_EXIST(HttpStatus.BAD_REQUEST, "AUTH_CODE_NOT_EXIST", "인증 코드가 존재하지 않습니다."),
    _AUTH_CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "AUTH_CODE_NOT_MATCH", "인증 코드가 일치하지 않습니다."),

    // Share Error
    _SHARE_URL_NOT_FOUND(HttpStatus.NOT_FOUND, "SHARE_URL_NOT_FOUND", "공유 url이 없습니다.");

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
    public HttpStatus getHttpStatus(){
        return httpStatus;
    }

    /**
     * 예외 코드를 반환하는 메서드
     *
     * @return 커스텀 코드값
     */
    @Override
    public String getCode(){
        return code;
    }

    /**
     * 예외 메시지를 반환하는 메서드
     *
     * @return 예외 메시지
     */
    @Override
    public String getErrorMsg(){
        return message;
    }
}
