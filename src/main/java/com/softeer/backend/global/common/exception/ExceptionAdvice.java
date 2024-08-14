package com.softeer.backend.global.common.exception;

import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsFailResponseDtoDto;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.response.ResponseDto;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;


/**
 * 예외를 한 곳에서 처리하는 클래스
 */
@Slf4j
@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    /**
     * GeneralException을 처리하는 메서드
     *
     * @param generalException 커스텀 예외의 최고 조상 클래스
     * @param webRequest       client 요청 객체
     * @return client 응답 객체
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleGeneralException(GeneralException generalException, WebRequest webRequest) {
        ResponseDto.ErrorReasonDto errorReasonHttpStatus = generalException.getErrorReason();
        return handleGeneralExceptionInternal(generalException, errorReasonHttpStatus, HttpHeaders.EMPTY, webRequest);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleEventLockException(EventLockException eventLockException, WebRequest webRequest) {
        return handleEventLockExceptionInternal(eventLockException, HttpHeaders.EMPTY, webRequest);
    }

    /**
     * ConstraintViolationException을 처리하는 메서드
     *
     * @param constraintViolationException 검증 예외
     * @param request                      client 요청 객체
     * @return client 응답 객체
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleValidationException(ConstraintViolationException constraintViolationException, WebRequest request) {

        List<String> errorMessages = constraintViolationException.getConstraintViolations().stream()
                .map(violation -> Optional.ofNullable(violation.getMessage()).orElse(""))
                .toList();

        return handleConstraintExceptionInternal(constraintViolationException, ErrorStatus._VALIDATION_ERROR, HttpHeaders.EMPTY, request,
                errorMessages);
    }

    /**
     * MethodArgumentNotValidException을 처리하는 메서드
     * <p>
     * ResponseEntityExceptionHandler의 메서드를 오버라이딩하여 사용한다.
     *
     * @param methodArgumentNotValidException 컨트롤러 메서드의 파라미터 객체에 대한 검증 예외
     * @param headers                         헤더 객체
     * @param status                          HttpStatusCode 값
     * @param request                         client 요청 객체
     * @return client 응답 객체
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException methodArgumentNotValidException,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        methodArgumentNotValidException.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
                    errors.merge(fieldName, errorMessage, (existingErrorMessage, newErrorMessage)
                            -> existingErrorMessage + ", " + newErrorMessage);
                });

        return handleArgsExceptionInternal(methodArgumentNotValidException, HttpHeaders.EMPTY, ErrorStatus._VALIDATION_ERROR, request, errors);
    }

    /**
     * 나머지 모든 예외들을 처리하는 메서드
     *
     * @param e       Exception을 상속한 예외 객체
     * @param request client 요청 객체
     * @return client 응답 객체
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleGlobalException(Exception e, WebRequest request) {

        return handleGlobalExceptionInternal(e, ErrorStatus._INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY, ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus(), request, e.getMessage());
    }

    /**
     * DB 관련 예외 처리
     */
    @ExceptionHandler
    public void handleDataAccessException(DataAccessException e) {
        log.error("DataAccessException occurred: {}", e.getMessage(), e);
    }

    // GeneralException에 대한 client 응답 객체를 생성하는 메서드
    private ResponseEntity<Object> handleGeneralExceptionInternal(Exception e, ResponseDto.ErrorReasonDto reason,
                                                                  HttpHeaders headers, WebRequest webRequest) {

        log.error("GeneralException captured in ExceptionAdvice", e);

        ResponseDto<Object> body = ResponseDto.onFailure(reason.getCode(), reason.getMessage(), null);

        return super.handleExceptionInternal(
                e,
                body,
                headers,
                reason.getHttpStatus(),
                webRequest
        );
    }

    // EventLockException에 대한 client 응답 객체를 생성하는 메서드
    private ResponseEntity<Object> handleEventLockExceptionInternal(EventLockException e, HttpHeaders headers, WebRequest webRequest) {

        log.error("EventLockException captured in ExceptionAdvice", e);

        String redissonKeyName = e.getRedissonKeyName();

        ResponseDto<Object> body = null;

//        if (redissonKeyName.contains("FCFS"))
//            body = ResponseDto.onSuccess(new FcfsFailResponseDtoDto(1));

        //TODO
        // DRAW 관련 예외일 경우, body 구성하는 코드 필요

        return super.handleExceptionInternal(
                e,
                body,
                headers,
                HttpStatus.OK,
                webRequest
        );
    }

    // ConstraintViolationException에 대한 client 응답 객체를 생성하는 메서드
    private ResponseEntity<Object> handleConstraintExceptionInternal(Exception e, ErrorStatus errorCommonStatus,
                                                                     HttpHeaders headers, WebRequest request,
                                                                     List<String> errorMessages) {

        log.error("ConstraintViolationException captured in ExceptionAdvice", e);

        ResponseDto<Object> body = ResponseDto.onFailure(errorCommonStatus.getCode(), errorCommonStatus.getMessage(), errorMessages);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorCommonStatus.getHttpStatus(),
                request
        );
    }

    // MethodArgumentNotValidException에 대한 client 응답 객체를 생성하는 메서드
    private ResponseEntity<Object> handleArgsExceptionInternal(Exception e, HttpHeaders headers, ErrorStatus errorCommonStatus,
                                                               WebRequest request, Map<String, String> errorArgs) {
        log.error("MethodArgumentNotValidException captured in ExceptionAdvice", e);

        ResponseDto<Object> body = ResponseDto.onFailure(errorCommonStatus.getCode(), errorCommonStatus.getMessage(), errorArgs);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorCommonStatus.getHttpStatus(),
                request
        );
    }

    // 나머지 모든 예외에 대한 client 응답 객체를 생성하는 메서드
    private ResponseEntity<Object> handleGlobalExceptionInternal(Exception e, ErrorStatus errorCommonStatus,
                                                                 HttpHeaders headers, HttpStatus status, WebRequest request, String errorPoint) {
        log.error("Exception captured in ExceptionAdvice", e);

        ResponseDto<Object> body = ResponseDto.onFailure(errorCommonStatus.getCode(), errorCommonStatus.getMessage(), errorPoint);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                status,
                request
        );
    }
}
