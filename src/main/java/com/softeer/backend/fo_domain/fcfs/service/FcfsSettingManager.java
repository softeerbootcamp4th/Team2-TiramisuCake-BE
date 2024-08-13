package com.softeer.backend.fo_domain.fcfs.service;

import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import com.softeer.backend.bo_domain.eventparticipation.repository.EventParticipationRepository;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsSettingDto;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import com.softeer.backend.global.common.constant.RedisLockPrefix;
import com.softeer.backend.global.util.EventLockRedisUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * 선착순 이벤트 정보를 관리하는 클래스
 */
@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class FcfsSettingManager {

    private List<FcfsSettingDto> fcfsSettingList;

    @Setter
    private boolean isFcfsClosed;

    private final FcfsSettingRepository fcfsSettingRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final EventLockRedisUtil eventLockRedisUtil;
    private final EventParticipationRepository eventParticipationRepository;

    private ScheduledFuture<?> scheduledFuture;

    @PostConstruct
    public void init() {
        loadInitialData();
//        scheduleTask();
    }

    public FcfsSettingDto getFcfsSettingByRound(int round){
        return fcfsSettingList.get(round-1);
    }

    /**
     * round 1에 해당하는 선착순 이벤트 속성으로 초기화
     */
    public void loadInitialData() {

        List<FcfsSetting> fcfsSettings = fcfsSettingRepository.findAll();
        fcfsSettingList = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            fcfsSettingList.add(null);  // 인덱스 0부터 3까지 빈 슬롯을 추가
        }

        fcfsSettings.forEach((fcfsSetting) -> {
            fcfsSettingList.set(fcfsSetting.getRound()-1, FcfsSettingDto.builder()
                    .round(fcfsSetting.getRound())
                    .startTime(fcfsSetting.getStartTime())
                    .endTime(fcfsSetting.getEndTime())
                    .winnerNum(fcfsSetting.getWinnerNum())
                    .build());
        });


    }

//    public void scheduleTask() {
//        scheduledFuture = taskScheduler.schedule(this::updateFcfsSetting, new CronTrigger("59 59 23 * * *"));
//    }

    /**
     * 1. 매일 23시 59분 59초에 스케줄러를 실행한다.
     * 2. 현재 시간이 이벤트의 endTime 이후라면 다음 round에 해당하는 이벤트 속성으로 설정한다.
     * 3. Redis에 저장된 선착순 이벤트 참여자 수를 DB에 저장하고 Redis에서 데이터를 삭제한다.
     */
//    @Transactional
//    protected void updateFcfsSetting() {
//        LocalDateTime now = LocalDateTime.now();
//        if (now.isAfter(endTime)) {
//            try {
//                FcfsSetting fcfsSetting = fcfsSettingRepository.findByRound(round + 1)
//                        .orElseThrow(() -> new IllegalStateException("Next FcfsSetting not found"));
//
//                this.round = fcfsSetting.getRound();
//                this.startTime = fcfsSetting.getStartTime();
//                this.endTime = fcfsSetting.getEndTime();
//                this.winnerNum = fcfsSetting.getWinnerNum();
//                this.isFcfsClosed = false;
//
//                log.info("FcfsSetting updated to round {}", round);
//
//                // TODO: 현재 날짜를 기준으로 하루 전 날짜로 방문자수, 추첨 및 선착순 참가자 수를 EventParticipation에 저장하는 로직 구현
//                int participantCount = eventLockRedisUtil.getData(RedisLockPrefix.FCFS_LOCK_PREFIX.getPrefix() + round);
//                EventParticipation eventParticipation = eventParticipationRepository.findSingleEventParticipation();
//                eventParticipation.addFcfsParticipantCount(participantCount);
//
//                eventLockRedisUtil.deleteParticipantCount(RedisLockPrefix.FCFS_PARTICIPANT_COUNT_PREFIX.getPrefix() + round);
//                eventLockRedisUtil.deleteParticipantIds(RedisLockPrefix.FCFS_LOCK_PREFIX.getPrefix() + (round - 1));
//
//            } catch (Exception e) {
//                log.info("Updating FcfsSetting is final");
//                stopScheduler();
//            }
//        }
//    }

    /**
     * Schedular의 작업을 비활성화 시키는 메서드
     */
    public void stopScheduler() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }


    public void setFcfsTime(List<FcfsSetting> fcfsSettingList) {
        fcfsSettingList
                .forEach((fcfsSetting) -> {
                    FcfsSettingDto fcfsSettingDto = this.fcfsSettingList.get(fcfsSetting.getRound());
                    fcfsSettingDto.setStartTime(fcfsSetting.getStartTime());
                    fcfsSettingDto.setEndTime(fcfsSetting.getEndTime());
                });
    }

    public void setFcfsWinnerNum(int fcfsWinnerNum) {
        fcfsSettingList.forEach((fcfsSettingDto) -> {
            fcfsSettingDto.setWinnerNum(fcfsWinnerNum);
        });
    }


}
