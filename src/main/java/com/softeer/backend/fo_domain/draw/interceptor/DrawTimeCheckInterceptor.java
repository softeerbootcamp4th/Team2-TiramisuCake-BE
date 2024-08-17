package com.softeer.backend.fo_domain.draw.interceptor;

import com.softeer.backend.fo_domain.draw.exception.DrawException;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DrawTimeCheckInterceptor implements HandlerInterceptor {
    private final DrawSettingManager drawSettingManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (CorsUtils.isPreFlightRequest(request))
            return true;

        if (!isAvailableTime()) {
            throw new DrawException(ErrorStatus._BAD_REQUEST);
        }

        return true;
    }

    private boolean isAvailableTime() {
        LocalDateTime now = LocalDateTime.now();

        return compareDate(now) && compareTime(now);
    }

    private boolean compareDate(LocalDateTime now) {
        LocalDateTime startDateTime = drawSettingManager.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = drawSettingManager.getEndDate().atStartOfDay();

        return now.isAfter(startDateTime) && now.isBefore(endDateTime);
    }

    private boolean compareTime(LocalDateTime now) {
        LocalDate nowDate = now.toLocalDate();
        LocalDateTime startTimeAsDateTime = LocalDateTime.of(nowDate, drawSettingManager.getStartTime());
        LocalDateTime endTimeAsDateTime = LocalDateTime.of(nowDate, drawSettingManager.getEndTime());

        return (now.isAfter(startTimeAsDateTime) && now.isBefore(endTimeAsDateTime));
    }
}
