package com.softeer.backend.fo_domain.fcfs.interceptor;

import com.softeer.backend.fo_domain.fcfs.exception.FcfsException;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcfsTimeCheckInterceptor implements HandlerInterceptor {

    private final FcfsSettingManager fcfsSettingManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        LocalDateTime now = LocalDateTime.now();

        if(!fcfsSettingManager.isFcfsEntryAvailable(now)){

            log.error("Cannot access the FCFS event");
            throw new FcfsException(ErrorStatus._BAD_REQUEST);
        }


        int round = fcfsSettingManager.getFcfsRound(now);
        request.setAttribute("round", round);


        return true;
    }
}