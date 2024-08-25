package com.softeer.backend.global.scheduler;

import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import com.softeer.backend.bo_domain.eventparticipation.repository.EventParticipationRepository;
import com.softeer.backend.fo_domain.draw.domain.Draw;
import com.softeer.backend.fo_domain.draw.repository.DrawRepository;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsRepository;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import com.softeer.backend.fo_domain.user.domain.User;
import com.softeer.backend.fo_domain.user.exception.UserException;
import com.softeer.backend.fo_domain.user.repository.UserRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import com.softeer.backend.global.util.DrawRedisUtil;
import com.softeer.backend.global.util.EventLockRedisUtil;
import com.softeer.backend.global.util.FcfsRedisUtil;
import com.softeer.backend.global.util.RandomCodeUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

/**
 * 정해진 시간에 데이터베이스에 값을 insert하는 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DbInsertScheduler {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final EventLockRedisUtil eventLockRedisUtil;
    private final FcfsRedisUtil fcfsRedisUtil;
    private final DrawRedisUtil drawRedisUtil;
    private final FcfsSettingManager fcfsSettingManager;
    private final DrawSettingManager drawSettingManager;
    private final EventParticipationRepository eventParticipationRepository;
    private final UserRepository userRepository;
    private final FcfsRepository fcfsRepository;
    private final DrawRepository drawRepository;
    private final RandomCodeUtil randomCodeUtil;


    private ScheduledFuture<?> scheduledFuture;

    @PostConstruct
    public void init() {
        scheduleTask();

    }

    public void scheduleTask() {
        scheduledFuture = taskScheduler.schedule(this::insertData, new CronTrigger("0 0 2 * * *"));
    }

    /**
     * 선착순 당첨자, 추첨 당첨자, 총 방문자 수, 선착순 참여자 수, 추첨 참여자 수를 데이터베이스에 저장하는 메서드
     */
    @Transactional
    protected void insertData() {
        LocalDate now = LocalDate.now();
        // 이벤트 기간이 아니라면 메서드 수행 x
        if (now.isBefore(drawSettingManager.getStartDate().plusDays(1)))
            return;

        // 이벤트 기간이 끝났다면 스케줄러 동작을 종료시킴
        if (now.isAfter(drawSettingManager.getEndDate().plusDays(1)))
            stopScheduler();

        // 총 방문자 수
        int totalVisitorsCount = eventLockRedisUtil.getData(RedisKeyPrefix.TOTAL_VISITORS_COUNT_PREFIX.getPrefix());
        eventLockRedisUtil.deleteData(RedisKeyPrefix.TOTAL_VISITORS_COUNT_PREFIX.getPrefix());

        // 선착순 이벤트 참여자 수
        int fcfsParticipantCount = 0;

        // 현재 추첨 이벤트의 라운드 받아오기
        if (fcfsSettingManager.getRoundForScheduler(now) != -1) {
            // fcfsClosed 변수 초기화
            fcfsSettingManager.setFcfsClosed(false);

            // 현재 추첨 이벤트의 라운드 받아오기
            int round = fcfsSettingManager.getRoundForScheduler(now);

            // 레디스에서 사용자와 코드 조회
            Map<String, Integer> participantIds = fcfsRedisUtil.getHashEntries(RedisKeyPrefix.FCFS_CODE_USERID_PREFIX.getPrefix() + round);
            participantIds.forEach((code, userId) -> {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> {
                            log.error("user not found in saveFcfsWinners method.");
                            return new UserException(ErrorStatus._NOT_FOUND);
                        });

                Fcfs fcfs = Fcfs.builder()
                        .user(user)
                        .round(round)
                        .code(code)
                        .winningDate(now.minusDays(1))
                        .build();

                // 코드와 사용자 저장
                fcfsRepository.save(fcfs);
            });

            // 선착순 참여자 수 가져오기
            fcfsParticipantCount += fcfsRedisUtil.getValue(RedisKeyPrefix.FCFS_PARTICIPANT_COUNT_PREFIX.getPrefix() + round);

            // 레디스에서 값들을 삭제
            fcfsRedisUtil.clearValue(RedisKeyPrefix.FCFS_PARTICIPANT_COUNT_PREFIX.getPrefix() + round);
            fcfsRedisUtil.clearIntegerSet(RedisKeyPrefix.FCFS_USERID_PREFIX.getPrefix() + round);
            fcfsRedisUtil.clearStringSet(RedisKeyPrefix.FCFS_CODE_PREFIX.getPrefix() + round);
            fcfsRedisUtil.clearHash(RedisKeyPrefix.FCFS_CODE_USERID_PREFIX.getPrefix() + round);
        }

        // drawParticipantCount에 추첨 이벤트 참가자 수 할당하기
        int drawParticipantCount = drawRedisUtil.getDrawParticipantCount();
        // redis에서 추첨 참가자 수 삭제
        drawRedisUtil.deleteDrawParticipantCount();

        // 추첨 당첨자 DB에 insert
        String drawWinnerKey;
        for (int ranking = 1; ranking < 4; ranking++) {
            drawWinnerKey = RedisKeyPrefix.DRAW_WINNER_LIST_PREFIX.getPrefix() + ranking;
            Set<Integer> winnerSet = drawRedisUtil.getAllDataAsSet(drawWinnerKey);

            LocalDate winningDate = LocalDate.now().minusDays(1);

            for (Integer userId : winnerSet) {
                User user = userRepository.findById(userId).orElseThrow(
                        () -> new UserException(ErrorStatus._NOT_FOUND));

                Draw draw = Draw.builder()
                        .user(user)
                        .rank(ranking)
                        .winningDate(winningDate)
                        .build();

                drawRepository.save(draw);
            }
        }

        // redis에서 추첨 당첨자 목록 삭제
        for (int ranking = 1; ranking < 4; ranking++) {
            drawWinnerKey = RedisKeyPrefix.DRAW_WINNER_LIST_PREFIX.getPrefix() + ranking;
            drawRedisUtil.deleteAllSetData(drawWinnerKey);
        }

        // 총 방문자 수, 선착순 참여자 수, 추첨 참여자 수 DB에 insert
        eventParticipationRepository.save(EventParticipation.builder()
                .visitorCount(totalVisitorsCount)
                .fcfsParticipantCount(fcfsParticipantCount)
                .drawParticipantCount(drawParticipantCount)
                .eventDate(now.minusDays(1))
                .build());

        if(fcfsSettingManager.getRoundForFcfsCode(now) != -1){

            int round = fcfsSettingManager.getRoundForFcfsCode(now);

            int i=0;
            while(i < fcfsSettingManager.getFcfsWinnerNum()){

                String code = makeFcfsCode(round);
                while (fcfsRedisUtil.isValueInStringSet(RedisKeyPrefix.FCFS_CODE_PREFIX.getPrefix() + round, code)) {
                    code = makeFcfsCode(round);
                }

                fcfsRedisUtil.addToStringSet(RedisKeyPrefix.FCFS_CODE_PREFIX.getPrefix() + round, code);

                i++;
            }

        }

    }

    private String makeFcfsCode(int round) {
        return (char) ('A' + round - 1) + randomCodeUtil.generateRandomCode(5);
    }

    /**
     * Scheduler의 작업을 비활성화 시키는 메서드
     */
    public void stopScheduler() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }
}
