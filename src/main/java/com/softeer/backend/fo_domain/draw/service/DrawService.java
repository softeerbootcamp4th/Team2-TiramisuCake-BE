package com.softeer.backend.fo_domain.draw.service;

import com.softeer.backend.fo_domain.draw.domain.DrawParticipationInfo;
import com.softeer.backend.fo_domain.draw.dto.main.DrawMainResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryResponseDto;
import com.softeer.backend.fo_domain.draw.exception.DrawException;
import com.softeer.backend.fo_domain.draw.repository.DrawParticipationInfoRepository;
import com.softeer.backend.fo_domain.draw.util.DrawResponseGenerateUtil;
import com.softeer.backend.fo_domain.draw.util.DrawUtil;
import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import com.softeer.backend.fo_domain.share.exception.ShareInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareInfoRepository;
import com.softeer.backend.global.annotation.EventLock;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import com.softeer.backend.global.util.DrawRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DrawService {
    private final DrawParticipationInfoRepository drawParticipationInfoRepository;
    private final ShareInfoRepository shareInfoRepository;
    private final DrawRedisUtil drawRedisUtil;
    private final DrawUtil drawUtil;
    private final DrawResponseGenerateUtil drawResponseGenerateUtil;
    private final DrawSettingManager drawSettingManager;

    /**
     * 1. 연속 참여일수 조회
     * 1-1. 만약 7일 연속 참여했다면 상품 정보 응답
     * 1-2. 만약 7일 미만 참여라면 일반 정보 응답
     */
    public DrawMainResponseDto getDrawMainPageInfo(Integer userId) {
        // 참여 정보 (연속참여일수) 조회
        DrawParticipationInfo drawParticipationInfo = drawParticipationInfoRepository.findDrawParticipationInfoByUserId(userId)
                .orElseThrow(() -> new DrawException(ErrorStatus._NOT_FOUND));

        // 출석일수 증가 및 유지 로직
        boolean isNewToday = handleAttendanceCount(userId, drawParticipationInfo);

        // 초대한 친구 수, 복권 기회 조회
        ShareInfo shareInfo = shareInfoRepository.findShareInfoByUserId(userId)
                .orElseThrow(() -> new ShareInfoException(ErrorStatus._NOT_FOUND));

        int drawParticipationCount = drawParticipationInfo.getDrawAttendanceCount();
        int invitedNum = shareInfo.getInvitedNum();
        int remainDrawCount = shareInfo.getRemainDrawCount();

        if (isNewToday) {
            // 연속 출석이라면
            drawParticipationCount += 1;
        } else {
            // 연속 출석이 아니라면 초기화
            drawParticipationCount = 1;
        }

        if (drawParticipationCount >= 7) {
            // 7일 연속 출석자라면
            return drawResponseGenerateUtil.generateMainFullAttendResponse(invitedNum, remainDrawCount, drawParticipationCount % 8);
        } else {
            // 연속 출석자가 아니라면
            return drawResponseGenerateUtil.generateMainNotAttendResponse(invitedNum, remainDrawCount, drawParticipationCount);
        }
    }

    private boolean handleAttendanceCount(Integer userId, DrawParticipationInfo drawParticipationInfo) {
        LocalDateTime lastParticipated = drawParticipationInfo.getLastParticipated();

        if (lastParticipated == null || isNewParticipateToday(lastParticipated)) {
            // 연속 출석이라면 연속출석일수 1 증가
            drawParticipationInfoRepository.increaseAttendanceCount(userId);
            return true;
        } else {
            // 연속출석이 아니라면 연속출석일수 1로 초기화
            drawParticipationInfoRepository.setAttendanceCountToOne(userId);
            return false;
        }
    }

    private boolean isNewParticipateToday(LocalDateTime lastParticipated) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = lastParticipated.plusDays(1).with(LocalTime.MIDNIGHT); // 마지막 접속일자의 다음날 자정
        LocalDateTime endDateTime = lastParticipated.plusDays(2).with(LocalTime.MIDNIGHT); // 마지막 접속일자의 2일 후 자정

        return (now.isAfter(startDateTime) && now.isBefore(endDateTime));
    }

    /**
     * 추첨 이벤트 당첨 로직 작성
     *
     * @param userId 사용자 아이디
     * @return 추첨 결과에 따른 응답 반환
     */
    public DrawModalResponseDto participateDrawEvent(Integer userId) {
        // 복권 기회 조회
        ShareInfo shareInfo = shareInfoRepository.findShareInfoByUserId(userId)
                .orElseThrow(() -> new ShareInfoException(ErrorStatus._NOT_FOUND));

        // 만약 남은 참여 기회가 0이라면
        if (shareInfo.getRemainDrawCount() == 0) {
            return drawResponseGenerateUtil.generateDrawLoserResponse(userId);
        }

        increaseDrawParticipationCount(); // 추첨 이벤트 참여자수 증가
        shareInfoRepository.decreaseRemainDrawCount(userId); // 횟수 1회 차감

        // 만약 당첨 목록에 존재한다면 이미 오늘은 한 번 당첨됐다는 뜻이므로 LoseModal 반환
        int ranking = getRankingIfWinner(userId); // 당첨 목록에 존재한다면 랭킹 반환
        if (ranking != 0) {
            drawParticipationInfoRepository.increaseLoseCount(userId);  // 낙첨 횟수 증가
            return drawResponseGenerateUtil.generateDrawLoserResponse(userId); // LoseModal 반환
        }

        // 당첨자 수 조회
        int first = drawSettingManager.getWinnerNum1(); // 1등 수
        int second = drawSettingManager.getWinnerNum2(); // 2등 수
        int third = drawSettingManager.getWinnerNum3(); // 3등 수

        // 당첨자 수 설정
        drawUtil.setFirst(first);
        drawUtil.setSecond(second);
        drawUtil.setThird(third);

        // 추첨 로직 실행
        drawUtil.performDraw();

        if (drawUtil.isDrawWin()) { // 당첨자일 경우
            ranking = drawUtil.getRanking();
            int winnerNum;
            if (ranking == 1) {
                winnerNum = first;
            } else if (ranking == 2) {
                winnerNum = second;
            } else {
                winnerNum = third;
            }

            if (isWinner(userId, ranking, winnerNum)) { // 레디스에 추첨 티켓이 남았다면, 레디스 당첨 목록에 추가
                // 추첨 티켓이 다 팔리지 않았다면
                drawParticipationInfoRepository.increaseWinCount(userId); // 당첨 횟수 증가
                return drawResponseGenerateUtil.generateDrawWinnerResponse(ranking); // WinModal 반환
            } else {
                // 추첨 티켓이 다 팔렸다면 로직상 당첨자라도 실패 반환
                drawParticipationInfoRepository.increaseLoseCount(userId);  // 낙첨 횟수 증가
                return drawResponseGenerateUtil.generateDrawLoserResponse(userId); // LoseModal 반환
            }
        } else { // 낙첨자일 경우
            drawParticipationInfoRepository.increaseLoseCount(userId);  // 낙첨 횟수 증가
            return drawResponseGenerateUtil.generateDrawLoserResponse(userId); // LoseModal 반환
        }
    }

    @EventLock(key = "DRAW_WINNER_#{#ranking}")
    private boolean isWinner(Integer userId, int ranking, int winnerNum) {
        String drawWinnerKey = RedisKeyPrefix.DRAW_WINNER_LIST_PREFIX.getPrefix() + ranking;
        Set<Integer> drawWinnerSet = drawRedisUtil.getAllDataAsSet(drawWinnerKey);

        // 레디스에서 해당 랭킹에 자리가 있는지 확인
        if (drawWinnerSet.size() < winnerNum) {
            // 자리가 있다면 당첨 성공. 당첨자 리스트에 추가
            drawRedisUtil.setIntegerValueToSet(drawWinnerKey, userId);
            return true;
        } else {
            // 이미 자리가 가득 차서 당첨 실패
            return false;
        }
    }

    @EventLock(key = "DRAW_PARTICIPATION_COUNT")
    private void increaseDrawParticipationCount() {
        drawRedisUtil.incrementIntegerValue(RedisKeyPrefix.DRAW_PARTICIPANT_COUNT_PREFIX.getPrefix());
    }

    /**
     * 당첨 내역 조회하는 메서드
     * 1. 당첨자라면 WinModal과 같은 당첨 내역 모달 응답
     * 2. 낙첨자라면 LoseModal과 같은 공유 url 모달 응답
     *
     * @param userId 사용자 아이디
     * @return 당첨 내역에 따른 응답
     */
    public DrawHistoryResponseDto getDrawHistory(Integer userId) {
        int ranking = getRankingIfWinner(userId);

        if (ranking != 0) {
            // 당첨자라면
            return drawResponseGenerateUtil.generateDrawHistoryWinnerResponse(ranking);
        }

        // 당첨자가 아니라면
        return drawResponseGenerateUtil.generateDrawHistoryLoserResponse(userId);
    }

    /**
     * userId가 당첨자 목록에 있으면 등수, 없으면 0 반환
     *
     * @param userId 사용자 아이디
     */
    private int getRankingIfWinner(int userId) {
        String drawWinnerKey;
        for (int ranking = 1; ranking < 4; ranking++) {
            drawWinnerKey = RedisKeyPrefix.DRAW_WINNER_LIST_PREFIX.getPrefix() + ranking;
            Set<Integer> drawTempSet = drawRedisUtil.getAllDataAsSet(drawWinnerKey);
            if (drawTempSet.contains(userId)) {
                return ranking;
            }
        }
        return 0;
    }
}
