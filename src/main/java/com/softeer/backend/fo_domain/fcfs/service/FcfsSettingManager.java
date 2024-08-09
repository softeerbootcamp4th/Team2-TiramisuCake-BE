package com.softeer.backend.fo_domain.fcfs.service;

import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import com.softeer.backend.bo_domain.eventparticipation.repository.EventParticipationRepository;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import com.softeer.backend.global.common.constant.RedisLockPrefix;
import com.softeer.backend.global.util.EventLockRedisUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

/**
 * 선착순 이벤트 정보를 관리하는 클래스
 */
@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class FcfsSettingManager {

    private int round;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int winnerNum;

    @Setter
    private boolean isFcfsClosed;

    private final FcfsSettingRepository fcfsSettingRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final EventLockRedisUtil eventLockRedisUtil;
    private final EventParticipationRepository eventParticipationRepository;

    private ScheduledFuture<?> scheduledFuture;

    @PostConstruct
    public void init(){
        loadInitialData();
        scheduleTask();
    }

    /**
     * round 1에 해당하는 선착순 이벤트 속성으로 초기화
     */
    public void loadInitialData() {
        try{
            FcfsSetting fcfsSetting = fcfsSettingRepository.findByRound(1)
                    .orElseThrow(IllegalStateException::new);

            this.round = fcfsSetting.getRound();
            this.startTime = fcfsSetting.getStartTime();
            this.endTime = fcfsSetting.getEndTime();
            this.winnerNum = fcfsSetting.getWinnerNum();
            this.isFcfsClosed = false;

        }
        catch(Exception e){
            log.error("FcfsSetting not found by round {}", round);
        }
    }

    public void scheduleTask() {
        scheduledFuture = taskScheduler.schedule(this::updateFcfsSetting, new CronTrigger("59 59 23 * * *"));
    }

    /**
     * 1. 매일 23시 59분 59초에 스케줄러를 실행한다.
     * 2. 현재 시간이 이벤트의 endTime 이후라면 다음 round에 해당하는 이벤트 속성으로 설정한다.
     * 3. Redis에 저장된 선착순 이벤트 참여자 수를 DB에 저장하고 Redis에서 데이터를 삭제한다.
     */
    @Transactional
    protected void updateFcfsSetting() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endTime)) {
            try {
                FcfsSetting fcfsSetting = fcfsSettingRepository.findByRound(round + 1)
                        .orElseThrow(() -> new IllegalStateException("Next FcfsSetting not found"));

                this.round = fcfsSetting.getRound();
                this.startTime = fcfsSetting.getStartTime();
                this.endTime = fcfsSetting.getEndTime();
                this.winnerNum = fcfsSetting.getWinnerNum();
                this.isFcfsClosed = false;

                log.info("FcfsSetting updated to round {}", round);

                int participantCount = eventLockRedisUtil.getData(RedisLockPrefix.FCFS_LOCK_PREFIX.getPrefix() + round);
                EventParticipation eventParticipation = eventParticipationRepository.findSingleEventParticipation();
                eventParticipation.addFcfsParticipantCount(participantCount);

                eventLockRedisUtil.deleteParticipantCount(RedisLockPrefix.FCFS_PARTICIPANT_COUNT_PREFIX.getPrefix() + round);
                eventLockRedisUtil.deleteParticipantIds(RedisLockPrefix.FCFS_LOCK_PREFIX.getPrefix() + (round-1));

            } catch (Exception e) {
                log.info("Updating FcfsSetting is final");
                stopScheduler();
            }
        }
    }

    /**
     * Schedular의 작업을 비활성화 시키는 메서드
     */
    public void stopScheduler() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    /**
     * Admin 기능으로 현재 round의 선착순 이벤트 정보를 변경했을 때, 변경 사항을 적용하기 위해 사용하는 메서드
     */
    public void setFcfsSetting(FcfsSetting fcfsSetting) {
        if(fcfsSetting.getRound() == this.round){
            this.startTime = fcfsSetting.getStartTime();
            this.endTime = fcfsSetting.getEndTime();
            this.winnerNum = fcfsSetting.getWinnerNum();
        }
    }

}
