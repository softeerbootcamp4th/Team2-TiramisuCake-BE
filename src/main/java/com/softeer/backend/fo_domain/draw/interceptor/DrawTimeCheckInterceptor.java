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

    /**
     * 참가 가능한 시간인지 확인
     * @return 참가 가능하면 true, 불가능하면 false 반환
     */
    private boolean isAvailableTime() {
        LocalDateTime now = LocalDateTime.now();

        return compareDate(now) && compareTime(now);
    }

    /**
     * 날짜 비교
     * @param now 현재시각
     * @return 참가 가능한 날짜이면 true, 불가능하면 false 반환
     */
    private boolean compareDate(LocalDateTime now) {
        LocalDateTime startDateTime = drawSettingManager.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = drawSettingManager.getEndDate().atStartOfDay();

        return now.isAfter(startDateTime) && now.isBefore(endDateTime);
    }

    /**
     * 시간 비교
     * @param now 현재 시각
     * @return 참가 가능한 시간이면 true, 불가능하면 false 반환
     */
    private boolean compareTime(LocalDateTime now) {
        LocalDate nowDate = now.toLocalDate();
        LocalDateTime startTimeAsDateTime = LocalDateTime.of(nowDate, drawSettingManager.getStartTime());
        LocalDateTime endTimeAsDateTime = LocalDateTime.of(nowDate, drawSettingManager.getEndTime());

        return (now.isAfter(startTimeAsDateTime) && now.isBefore(endTimeAsDateTime));
    }
}
