package com.softeer.backend.fo_domain.fcfs.service;

import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsFailResponse;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsResponse;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsSuccessResponse;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsRepository;
import com.softeer.backend.fo_domain.user.domain.User;
import com.softeer.backend.fo_domain.user.exception.UserException;
import com.softeer.backend.fo_domain.user.repository.UserRepository;
import com.softeer.backend.global.annotation.EventLock;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.util.EventLockRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 선착순 관련 이벤트를 처리하는 클래스
 */
@Service
@RequiredArgsConstructor
public class FcfsService {
    private static final String FCFS_LOCK_PREFIX = "LOCK:FCFS_";

    FcfsSettingManager fcfsSettingManager;
    FcfsRepository fcfsRepository;
    EventLockRedisUtil eventLockRedisUtil;
    UserRepository userRepository;

    public FcfsResponse handleFcfsEvent(int userId){
        if(fcfsSettingManager.isFcfsClosed())
            return countFcfsLosers(fcfsSettingManager.getRound());

        return saveFcfsWinners(userId, fcfsSettingManager.getRound());
    }

    @EventLock(key = "FCFS_#{#round}")
    private FcfsResponse saveFcfsWinners(int userId, int round) {
        int fcfsWinnerCount = eventLockRedisUtil.getData(FCFS_LOCK_PREFIX + round);

        if(fcfsWinnerCount < fcfsSettingManager.getWinnerNum()){
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserException(ErrorStatus._USER_NOT_FOUND));

            Fcfs fcfs = Fcfs.builder()
                    .user(user)
                    .round(round)
                    .winningDate(LocalDateTime.now())
                    .build();
            fcfsRepository.save(fcfs);

            return new FcfsSuccessResponse();
        }
        else{
            fcfsSettingManager.setFcfsClosed(true);

            return new FcfsFailResponse();
        }
    }

    private FcfsFailResponse countFcfsLosers(int round){
        eventLockRedisUtil.incrementData(FCFS_LOCK_PREFIX + round);

        return new FcfsFailResponse();
    }
}
