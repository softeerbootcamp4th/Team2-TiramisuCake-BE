package com.softeer.backend.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.softeer.backend.global.common.code.BaseCode;
import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.code.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Client 응답 객체 클래스
 *
 * @param <T> 응답에 담을 객체 타입
 */
@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ResponseDto<T> {

    // client 요청 처리 성공 여부값
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    // 커스텀 상태 코드값
    private final String code;
    // 응답 메시지
    private final String message;

    // 응답에 담을 객체
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    /**
     * 요청 처리에는 성공했지만, 보낼 데이터가 없을 경우 사용하는 메서드
     */
    public static <T> ResponseDto<T> onSuccess() {
        return new ResponseDto<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), null);
    }

    /**
     * client 요청 처리 성공 시의 응답값을 생성하는 메서드
     *
     * @param result client 응답에 넣을 객체
     * @param <T>    응답에 담을 객체 타입
     * @return client 응답 객체
     */
    public static <T> ResponseDto<T> onSuccess(T result) {
        return new ResponseDto<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), result);
    }

    /**
     * client 요청 처리 성공 시의 응답값을 생성하는 메서드
     *
     * @param code   성공 응답 코드
     * @param result client 응답에 넣을 객체
     * @param <T>    응답에 담을 객체 타입
     * @return client 응답 객체
     */
    public static <T> ResponseDto<T> onSuccess(BaseCode code, T result) {
        return new ResponseDto<>(true, code.getCode(), code.getMsg(), result);
    }

    /**
     * client 요청 처리 실패 시의 응답값을 생성하는 메서드
     *
     * @param code 실패 응답 코드
     * @param <T>  응답에 담을 객체 타입
     * @return client 응답 객체
     */
    public static <T> ResponseDto<T> onFailure(BaseErrorCode code) {
        return new ResponseDto<>(false, code.getCode(), code.getErrorMsg(), null);
    }

    /**
     * client 요청 처리 실패 시의 응답값을 생성하는 메서드
     *
     * @param code    code 실패 응답 코드
     * @param message 실패 응답 메시지
     * @param result  client 응답에 넣을 객체
     * @param <T>     응답에 담을 객체 타입
     * @return client 응답 객체
     */
    public static <T> ResponseDto<T> onFailure(String code, String message, T result) {
        return new ResponseDto<>(false, code, message, result);
    }

    /**
     * Error 정보를 갖고 있는 내부 클래스
     */
    @Getter
    @Builder
    public static class ErrorReasonDto {
        private HttpStatus httpStatus;
        private final boolean isSuccess;
        private final String code;
        private final String message;
    }

    /**
     * 성공 응답 정보를 갖고 있는 내부 클래스
     */
    @Getter
    @Builder
    public static class ReasonDto {
        private HttpStatus httpStatus;
        private final boolean isSuccess;
        private final String code;
        private final String message;
    }
}
