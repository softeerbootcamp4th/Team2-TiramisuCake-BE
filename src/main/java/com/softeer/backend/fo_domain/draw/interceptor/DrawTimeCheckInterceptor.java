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

/**
 * 추첨 이벤트의 참여시각을 확인하는 인터셉터
 */
@Component
@RequiredArgsConstructor
public class DrawTimeCheckInterceptor implements HandlerInterceptor {
    private final DrawSettingManager drawSettingManager;

    /**
     * 추첨 이벤트 컨트롤러로 요청이 들어가기 전에 실행
     *
     * 1. 프리플라이트 요청이면 true 반환
     * 2. 참여 가능한 시간이 아니면 예외 던지기
     */
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
     *
     * 참가 가능하면 true, 불가능하면 false 반환
     */
    private boolean isAvailableTime() {
        LocalDateTime now = LocalDateTime.now();

        return compareDate(now) && compareTime(now);
    }

    /**
     * 날짜를 비교하는 메서드
     * 참가 가능한 날짜이면 true, 불가능하면 false 반환
     */
    private boolean compareDate(LocalDateTime now) {
        LocalDateTime startDateTime = drawSettingManager.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = drawSettingManager.getEndDate().atStartOfDay();

        return now.isAfter(startDateTime) && now.isBefore(endDateTime);
    }

    /**
     * 시간을 비교하는 메서드
     * @return 참가 가능한 시간이면 true, 불가능하면 false 반환
     */
    private boolean compareTime(LocalDateTime now) {
        LocalDate nowDate = now.toLocalDate();
        LocalDateTime startTimeAsDateTime = LocalDateTime.of(nowDate, drawSettingManager.getStartTime());
        LocalDateTime endTimeAsDateTime = LocalDateTime.of(nowDate, drawSettingManager.getEndTime());

        return (now.isAfter(startTimeAsDateTime) && now.isBefore(endTimeAsDateTime));
    }
}
