package com.softeer.backend.fo_domain.draw.service;

import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.exception.DrawException;
import com.softeer.backend.fo_domain.draw.repository.DrawRepository;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.user.repository.UserRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.util.DrawRedisUtil;
import com.softeer.backend.global.util.EventLockRedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 추첨 이벤트 설정을 관리하기 위한 클래스
 */
@Getter
@Component
@RequiredArgsConstructor
public class DrawSettingManager {
    private final DrawRepository drawRepository;
    private final DrawSettingRepository drawSettingRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final EventLockRedisUtil eventLockRedisUtil;
    private final DrawRedisUtil drawRedisUtil;
    private final UserRepository userRepository;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int winnerNum1;
    private int winnerNum2;
    private int winnerNum3;

    // @PostConstruct로 생성됐을 시 세팅정보 가져오기
    // 스케줄러로 01:00:00에 redis 임시 목록 삭제하기

    /**
     * 서버가 실행되고 인스턴스가 만들어진 직후 DB로부터 해당 설정을 불러와 저장하는 메서드
     */
    @PostConstruct
    public void initializeDrawSettingManager() {
        DrawSetting drawSetting = drawSettingRepository.findById(1)
                .orElseThrow(() -> new DrawException(ErrorStatus._NOT_FOUND));

        startDate = drawSetting.getStartDate();
        endDate = drawSetting.getEndDate();
        startTime = drawSetting.getStartTime();
        endTime = drawSetting.getEndTime();
        winnerNum1 = drawSetting.getWinnerNum1();
        winnerNum2 = drawSetting.getWinnerNum2();
        winnerNum3 = drawSetting.getWinnerNum3();
    }

    /**
     * 추첨이벤트 정보를 설정하는 메서드
     */
    public void setDrawSetting(DrawSetting drawSetting) {
        this.startDate = drawSetting.getStartDate();
        this.endDate = drawSetting.getEndDate();
        this.startTime = drawSetting.getStartTime();
        this.endTime = drawSetting.getEndTime();

        this.winnerNum1 = drawSetting.getWinnerNum1();
        this.winnerNum2 = drawSetting.getWinnerNum2();
        this.winnerNum3 = drawSetting.getWinnerNum3();
    }

}
