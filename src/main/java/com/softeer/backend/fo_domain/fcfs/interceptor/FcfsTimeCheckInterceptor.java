package com.softeer.backend.fo_domain.fcfs.interceptor;

import com.softeer.backend.fo_domain.fcfs.exception.FcfsException;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

/**
 * 선착순 이벤트가 활성화 된 시간에 들어온 요청인지 검사하는 인터셉터 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FcfsTimeCheckInterceptor implements HandlerInterceptor {

    private final FcfsSettingManager fcfsSettingManager;

    /**
     * 선착순 등록 요청을 검사하는 메서드
     * <p>
     * 1-1. 선착순 이벤트가 활성화 되지 않았다면 예외가 발생한다.
     * 1-2. 활성화 되었다면 round값을 request를 통해 controller단으로 넘긴다.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (CorsUtils.isPreFlightRequest(request))
            return true;

        LocalDateTime now = LocalDateTime.now();

        if (!fcfsSettingManager.isFcfsEntryAvailable(now)) {

            if("GET".equalsIgnoreCase(request.getMethod())){
                log.error("Cannot access the FCFS event");
                throw new FcfsException(ErrorStatus._BAD_REQUEST);
            }

            else if("POST".equalsIgnoreCase(request.getMethod())){
                log.error("Cannot participate FCFS event");
                throw new FcfsException(ErrorStatus._FCFS_ALREADY_CLOSED);
            }

        }


        int round = fcfsSettingManager.getFcfsRound(now);
        request.setAttribute("round", round);


        return true;
    }
}