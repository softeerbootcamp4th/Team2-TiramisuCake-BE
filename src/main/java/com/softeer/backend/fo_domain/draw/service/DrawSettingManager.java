package com.softeer.backend.fo_domain.draw.service;

import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.exception.DrawException;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RedisLockPrefix;
import com.softeer.backend.global.util.EventLockRedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Getter
@Component
@RequiredArgsConstructor
public class DrawSettingManager {
    private final DrawSettingRepository drawSettingRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final EventLockRedisUtil eventLockRedisUtil;

    private Date startTime;
    private Date endTime;
    private int winnerNum1;
    private int winnerNum2;
    private int winnerNum3;

    // @PostConstruct로 생성됐을 시 세팅정보 가져오기
    // 스케줄러로 01:00:00에 redis 임시 목록 삭제하기

    @PostConstruct
    public void initializeDrawSettingManager() {
        DrawSetting drawSetting = drawSettingRepository.findById(1)
                .orElseThrow(() -> new DrawException(ErrorStatus._NOT_FOUND));

        startTime = drawSetting.getStartTime();
        endTime = drawSetting.getEndTime();
        winnerNum1 = drawSetting.getWinnerNum1();
        winnerNum2 = drawSetting.getWinnerNum2();
        winnerNum3 = drawSetting.getWinnerNum3();

        // 매일 01:00:00에 redis 임시 당첨자 목록 삭제하기
        taskScheduler.schedule(this::deleteTempWinnerSetFromRedis, new CronTrigger("0 0 1 * * *"));
    }

    private void deleteTempWinnerSetFromRedis() {
        String drawTempKey;
        for (int ranking = 1; ranking < 4; ranking++) {
            drawTempKey = RedisLockPrefix.DRAW_TEMP_PREFIX.getPrefix() + ranking;
            eventLockRedisUtil.deleteTempWinnerList(drawTempKey);
        }
    }
}
