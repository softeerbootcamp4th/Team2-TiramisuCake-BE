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
import com.softeer.backend.global.common.constant.LockPrefix;
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

    FcfsSettingManager fcfsSettingManager;
    FcfsRepository fcfsRepository;
    EventLockRedisUtil eventLockRedisUtil;
    UserRepository userRepository;

    /**
     * 1. 선착순 당첨자가 아직 다 결정되지 않았으면, 선착순 당첨 응답 생성 및 반환
     * 2. 선착순 당첨자가 다 결정됐다면, Redisson lock을 사용하지 않고 Redis에 저장된 선착순 이벤트 참여자 수를 1명씩 더한다.
     */
    public FcfsResponse handleFcfsEvent(int userId){
        if(fcfsSettingManager.isFcfsClosed())
            return countFcfsLosers(fcfsSettingManager.getRound());

        return saveFcfsWinners(userId, fcfsSettingManager.getRound());
    }

    /**
     * 1. Redisson lock을 걸고 선착순 이벤트 참여자 수가 지정된 수보다 적다면, 선착순 당첨 정보를 DB에 저장하고
     *    Redis에 저장된 선착순 이벤트 참여자 수를 1만큼 증가시키도 선착순 당첨 응답을 생성하여 반환한다.
     *    만약, 참여자 수가 총 당첨자 수와 같아졌으면, fcfsSettingManager의 setFcfsClosed를 true로 변환한다.
     * 2. setFcfsClosed가 true로 바뀌게 전에 요청이 들어왔다면, 선착순 실패 응답을 생성하여 반환한다.
     */
    @EventLock(key = "FCFS_#{#round}")
    private FcfsResponse saveFcfsWinners(int userId, int round) {
        int fcfsWinnerCount = eventLockRedisUtil.getData(LockPrefix.FCFS_LOCK_PREFIX.getPrefix() + round);

        if(fcfsWinnerCount < fcfsSettingManager.getWinnerNum()){
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserException(ErrorStatus._USER_NOT_FOUND));

            Fcfs fcfs = Fcfs.builder()
                    .user(user)
                    .round(round)
                    .winningDate(LocalDateTime.now())
                    .build();
            fcfsRepository.save(fcfs);

            eventLockRedisUtil.incrementData(LockPrefix.FCFS_LOCK_PREFIX.getPrefix() + round);
            if(fcfsWinnerCount + 1 == fcfsSettingManager.getWinnerNum()){
                fcfsSettingManager.setFcfsClosed(true);
            }

            return new FcfsSuccessResponse();
        }
        else{
            return new FcfsFailResponse();
        }
    }

    private FcfsFailResponse countFcfsLosers(int round){
        eventLockRedisUtil.incrementData(LockPrefix.FCFS_LOCK_PREFIX.getPrefix() + round);

        return new FcfsFailResponse();
    }

}
