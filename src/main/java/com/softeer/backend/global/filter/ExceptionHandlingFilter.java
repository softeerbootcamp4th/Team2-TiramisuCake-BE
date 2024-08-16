package com.softeer.backend.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeer.backend.global.common.code.BaseErrorCode;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.exception.JwtAuthenticationException;
import com.softeer.backend.global.common.response.ResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Jwt 예외를 처리하는 필터 클래스
 */
@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
            // Jwt 인증 예외 처리
        } catch (JwtAuthenticationException jwtAuthenticationException) {

            log.error("JwtAuthenticationException occurs in ExceptionHandlingFilter",
                    jwtAuthenticationException);

            setErrorResponse(response, jwtAuthenticationException.getCode());

            // 나머지 예외 처리
        } catch (Exception e) {

            log.error("Exception occurs in ExceptionHandlingFilter", e);

            setErrorResponse(response, ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    // 인증 예외 처리 메서드
    private void setErrorResponse(HttpServletResponse response,
                                  BaseErrorCode errorCode) {


        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (OutputStream os = response.getOutputStream()) {

            objectMapper.writeValue(os, ResponseDto.onFailure(errorCode));
            os.flush();

        } catch (IOException e) {

            log.error("IOException occurs in ExceptionHandlingFilter", e);
        }
    }
}
