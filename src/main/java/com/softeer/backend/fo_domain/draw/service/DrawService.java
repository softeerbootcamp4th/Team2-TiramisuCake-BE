package com.softeer.backend.fo_domain.draw.service;

import com.softeer.backend.fo_domain.draw.domain.DrawParticipationInfo;
import com.softeer.backend.fo_domain.draw.dto.*;
import com.softeer.backend.fo_domain.draw.exception.DrawException;
import com.softeer.backend.fo_domain.draw.repository.DrawParticipationInfoRepository;
import com.softeer.backend.fo_domain.draw.util.DrawUtil;
import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import com.softeer.backend.fo_domain.share.exception.ShareInfoException;
import com.softeer.backend.fo_domain.share.exception.ShareUrlInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareInfoRepository;
import com.softeer.backend.fo_domain.share.repository.ShareUrlInfoRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RedisLockPrefix;
import com.softeer.backend.global.common.response.ResponseDto;
import com.softeer.backend.global.util.EventLockRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class DrawService {
    // private final DrawRepository drawRepository;
    private final DrawParticipationInfoRepository drawParticipationInfoRepository;
    private final ShareInfoRepository shareInfoRepository;
    private final ShareUrlInfoRepository shareUrlInfoRepository;
    private final EventLockRedisUtil eventLockRedisUtil;
    private final DrawSettingManager drawSettingManager;
    private final DrawUtil drawUtil;

    /**
     * 1. redis의 임시 당첨 목록에 존재하는지 확인
     * 1-1. 있으면 해당 등수에 맞는 응답 만들어서 반환
     * 1-1-1. 만약 7일 연속 출석했다면 그에 맞는 응답 만들어서 반환
     * 1-1-2. 만약 연속 출석을 못했다면 그에 맞는 응답 만들어서 반환
     * 1-2. 없으면 새로 등수 계산
     * 2. 당첨되었다면 레디스에 저장 후 당첨 응답 반환
     * 2-1. 만약 7일 연속 출석했다면 그에 맞는 응답 만들어서 반환
     * 2-2. 만약 연속 출석을 못했다면 그에 맞는 응답 만들어서 반환
     * 3. 낙첨되었다면 당첨 실패 응답 반환
     * 3-1. 만약 7일 연속 출석했다면 그에 맞는 응답 만들어서 반환
     * 3-2. 만약 연속 출석을 못했다면 그에 맞는 응답 만들어서 반환
     */
    public ResponseDto<DrawResponseDto> getDrawMainPageInfo(Integer userId) {
        // 참여 정보 (연속참여일수) 조회
        DrawParticipationInfo drawParticipationInfo = drawParticipationInfoRepository.findDrawParticipationInfoByUserId(userId)
                .orElseThrow(() -> new DrawException(ErrorStatus._NOT_FOUND));

        // 초대한 친구 수, 복권 기회 조회
        ShareInfo shareInfo = shareInfoRepository.findShareInfoByUserId(userId)
                .orElseThrow(() -> new ShareInfoException(ErrorStatus._NOT_FOUND));

        int drawParticipationCount = drawParticipationInfo.getDrawParticipationCount();
        int invitedNum = shareInfo.getInvitedNum();
        int remainDrawCount = shareInfo.getRemainDrawCount();

        // 만약 임시 당첨 목록에 존재한다면 등수에 맞는 응답 만들어서 반환
        int ranking = getRankingIfWinner(userId);
        if (ranking != 0) {
            drawUtil.setRanking(ranking);


            if (drawParticipationCount < 7) {
                // 만약 연속 출석하지 못했다면
                return ResponseDto.onSuccess(DrawWinNotAttendResponseDto.builder()
                        .invitedNum(invitedNum)
                        .remainDrawCount(remainDrawCount)
                        .drawParticipationCount(drawParticipationCount)
                        .isDrawWin(true)
                        .images(drawUtil.generateWinImages())
                        .winModal(drawUtil.generateWinModal())
                        .build());
            } else {
                // 7일 연속 출석했다면
                return ResponseDto.onSuccess(DrawWinFullAttendResponseDto.builder()
                        .invitedNum(invitedNum)
                        .remainDrawCount(remainDrawCount)
                        .drawParticipationCount(drawParticipationCount)
                        .isDrawWin(true)
                        .images(drawUtil.generateWinImages())
                        .winModal(drawUtil.generateWinModal())
                        .fullAttendModal(drawUtil.generateWinFullAttendModal())
                        .build());
            }
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
            // redis 임시 당첨자 목록에 저장
            saveWinnerInfo(drawUtil.getRanking(), userId);

            if (drawParticipationCount < 7) {
                // 만약 연속 출석하지 못했다면
                return ResponseDto.onSuccess(DrawWinNotAttendResponseDto.builder()
                        .invitedNum(invitedNum)
                        .remainDrawCount(remainDrawCount)
                        .drawParticipationCount(drawParticipationCount)
                        .isDrawWin(true)
                        .images(drawUtil.generateWinImages())
                        .winModal(drawUtil.generateWinModal())
                        .build());
            } else {
                // 7일 연속 출석했다면
                return ResponseDto.onSuccess(DrawWinFullAttendResponseDto.builder()
                        .invitedNum(invitedNum)
                        .remainDrawCount(remainDrawCount)
                        .drawParticipationCount(drawParticipationCount)
                        .isDrawWin(true)
                        .images(drawUtil.generateWinImages())
                        .winModal(drawUtil.generateWinModal())
                        .fullAttendModal(drawUtil.generateWinFullAttendModal())
                        .build());
            }

        } else { // 낙첨자일 경우
            String shareUrl = shareUrlInfoRepository.findShareUrlByUserId(userId)
                    .orElseThrow(() -> new ShareUrlInfoException(ErrorStatus._NOT_FOUND));

            if (drawParticipationCount < 7) {
                // 만약 연속 출석하지 못했다면
                return ResponseDto.onSuccess(DrawLoseNotAttendResponseDto.builder()
                        .invitedNum(invitedNum)
                        .remainDrawCount(remainDrawCount)
                        .drawParticipationCount(drawParticipationCount)
                        .isDrawWin(false)
                        .images(drawUtil.generateLoseImages())
                        .loseModal(drawUtil.generateLoseModal(shareUrl))
                        .build());
            } else {
                // 7일 연속 출석했다면
                return ResponseDto.onSuccess(DrawLoseFullAttendResponseDto.builder()
                        .invitedNum(invitedNum)
                        .remainDrawCount(remainDrawCount)
                        .drawParticipationCount(drawParticipationCount)
                        .isDrawWin(false)
                        .images(drawUtil.generateLoseImages())
                        .loseModal(drawUtil.generateLoseModal(shareUrl))
                        .fullAttendModal(drawUtil.generateLoseFullAttendModal())
                        .build());
            }

        }
    }

    public ResponseDto<DrawModalResponseDto> participateDrawEvent(Integer userId) {
        // 복권 기회 조회
        ShareInfo shareInfo = shareInfoRepository.findShareInfoByUserId(userId)
                .orElseThrow(() -> new ShareInfoException(ErrorStatus._NOT_FOUND));

        int invitedNum = shareInfo.getInvitedNum();
        int remainDrawCount = shareInfo.getRemainDrawCount();

        // 만약 남은 참여 기회가 0이라면
        if (remainDrawCount == 0) {
            return ResponseDto.onSuccess(responseLoseModal(userId));
        }

        // 만약 당첨 목록에 존재한다면 이미 오늘은 한 번 당첨됐다는 뜻이므로 LoseModal 반환
        int ranking = getRankingIfWinner(userId);
        if (ranking != 0) {
            decreaseRemainDrawCount(userId, invitedNum, remainDrawCount); // 횟수 1회 차감
            return ResponseDto.onSuccess(responseLoseModal(userId)); // LoseModal 반환
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
            decreaseRemainDrawCount(userId, invitedNum, remainDrawCount);  // 횟수 1회 차감
            saveWinnerInfo(drawUtil.getRanking(), userId); // redis 당첨자 목록에 저장
            return ResponseDto.onSuccess(responseWinModal()); // WinModal 반환
        } else { // 낙첨자일 경우
            decreaseRemainDrawCount(userId, invitedNum, remainDrawCount);  // 횟수 1회 차감
            return ResponseDto.onSuccess(responseLoseModal(userId));  // LoseModal 반환
        }
    }

    private DrawLoseModalResponseDto responseLoseModal(Integer userId) {
        String shareUrl = shareUrlInfoRepository.findShareUrlByUserId(userId)
                .orElseThrow(() -> new ShareUrlInfoException(ErrorStatus._NOT_FOUND));

        return DrawLoseModalResponseDto.builder()
                .isDrawWin(false)
                .shareUrl(shareUrl)
                .build();
    }

    private DrawWinModalResponseDto responseWinModal() {
        return drawUtil.generateWinModal();
    }

    private void decreaseRemainDrawCount(Integer userId, int invitedNum, int remainDrawCount) {
        // 횟수 1회 차감
        int newRemainDrawCount = remainDrawCount - 1;
        ShareInfo shareInfo = ShareInfo.builder()
                .userId(userId)
                .invitedNum(invitedNum)
                .remainDrawCount(newRemainDrawCount)
                .build();

        shareInfoRepository.save(shareInfo);
    }

    /**
     * redis 임시 당첨자 목록에 저장
     *
     * @param ranking redis의 키로 사용될 등수
     * @param userId  사용자 아이디
     */
    private void saveWinnerInfo(int ranking, int userId) {
        String drawTempKey = RedisLockPrefix.DRAW_TEMP_PREFIX.getPrefix() + ranking;
        eventLockRedisUtil.addValueToSet(drawTempKey, userId);
    }

    /**
     * userId가 임시 당첨자 목록에 있으면 등수, 없으면 0 반환
     *
     * @param userId
     */
    private int getRankingIfWinner(int userId) {
        String drawTempKey;
        for (int ranking = 1; ranking < 4; ranking++) {
            drawTempKey = RedisLockPrefix.DRAW_TEMP_PREFIX.getPrefix() + ranking;
            Set<Integer> drawTempSet = eventLockRedisUtil.getAllDataAsSet(drawTempKey);
            if (drawTempSet.contains(userId)) {
                return ranking;
            }
        }
        return 0;
    }
}
