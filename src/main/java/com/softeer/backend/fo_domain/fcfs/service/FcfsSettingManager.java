package com.softeer.backend.fo_domain.fcfs.service;

import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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

    @PostConstruct
    public void init(){
        loadInitialData();
    }

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
            log.error("FcfsSetting not found by 'findByRound' sql");
        }
    }

}
