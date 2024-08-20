package com.softeer.backend.fo_domain.fcfs.service;

import com.softeer.backend.bo_domain.eventparticipation.repository.EventParticipationRepository;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.domain.Quiz;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsSettingDto;
import com.softeer.backend.fo_domain.fcfs.dto.QuizDto;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import com.softeer.backend.fo_domain.fcfs.repository.QuizRepository;
import com.softeer.backend.global.util.EventLockRedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 선착순 이벤트 정보를 관리하는 클래스
 */
@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class FcfsSettingManager {

    private final FcfsSettingRepository fcfsSettingRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final EventLockRedisUtil eventLockRedisUtil;
    private final EventParticipationRepository eventParticipationRepository;
    private final QuizRepository quizRepository;

    private List<FcfsSettingDto> fcfsSettingList;

    @Setter
    private boolean isFcfsClosed = false;

    @PostConstruct
    public void init() {
        loadInitialData();
    }

    public FcfsSettingDto getFcfsSettingByRound(int round) {
        return fcfsSettingList.get(round - 1);
    }

    /**
     * round 1에 해당하는 선착순 이벤트 속성으로 초기화
     */
    public void loadInitialData() {

        List<FcfsSetting> fcfsSettings = fcfsSettingRepository.findAll();
        fcfsSettingList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            fcfsSettingList.add(null);  // 인덱스 0부터 3까지 빈 슬롯을 추가
        }

        fcfsSettings.forEach((fcfsSetting) -> {
            fcfsSettingList.set(fcfsSetting.getRound() - 1, FcfsSettingDto.builder()
                    .round(fcfsSetting.getRound())
                    .startTime(fcfsSetting.getStartTime())
                    .endTime(fcfsSetting.getEndTime())
                    .winnerNum(fcfsSetting.getWinnerNum())
                    .build());
        });
    }

    public void setFcfsSettingList(List<FcfsSetting> fcfsSettings){

        fcfsSettings.forEach((fcfsSetting) -> {
            fcfsSettingList.set(fcfsSetting.getRound() - 1, FcfsSettingDto.builder()
                    .round(fcfsSetting.getRound())
                    .startTime(fcfsSetting.getStartTime())
                    .endTime(fcfsSetting.getEndTime())
                    .winnerNum(fcfsSetting.getWinnerNum())
                    .build());
        });
    }

    public int getRoundForScheduler(LocalDate localDate) {
        for (FcfsSettingDto fcfsSettingDto : fcfsSettingList) {
            if (fcfsSettingDto != null) {
                LocalDate startDate = fcfsSettingDto.getStartTime().toLocalDate();
                LocalDate dayAfterStartDate = startDate.plusDays(1);

                // localDate가 startDate의 하루 다음날과 같은지 확인
                if (localDate.equals(dayAfterStartDate)) {
                    return fcfsSettingDto.getRound();
                }
            }
        }
        return -1;  // 해당하는 데이터가 없는 경우
    }

    public int getFcfsWinnerNum(){
        return fcfsSettingList.get(0).getWinnerNum();
    }

    public boolean isFcfsEntryAvailable(LocalDateTime now){
        for(FcfsSettingDto fcfsSettingDto : fcfsSettingList){
            LocalDateTime startTime = fcfsSettingDto.getStartTime();
            LocalDateTime endTime = fcfsSettingDto.getEndTime();

            if((now.isEqual(startTime) || now.isAfter(startTime))
                    && (now.isEqual(endTime) || now.isBefore(endTime))) {
                return true;
            }
        }
        return false;
    }

    public Integer getFcfsRound(LocalDateTime now){

        for(FcfsSettingDto fcfsSettingDto : fcfsSettingList){
            LocalDateTime startTime = fcfsSettingDto.getStartTime();
            LocalDateTime endTime = fcfsSettingDto.getEndTime();

            if((now.isEqual(startTime) || now.isAfter(startTime))
                    && (now.isEqual(endTime) || now.isBefore(endTime))) {
                return fcfsSettingDto.getRound();
            }
        }
        return null;
    }

    public LocalDateTime getNextFcfsTime(LocalDateTime now){

        for(FcfsSettingDto fcfsSettingDto : fcfsSettingList){
            LocalDateTime startTime = fcfsSettingDto.getStartTime();

            if(now.isBefore(startTime)) {
                return startTime;
            }
        }
        return null;
    }

}
