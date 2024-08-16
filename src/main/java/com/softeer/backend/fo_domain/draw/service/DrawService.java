package com.softeer.backend.fo_domain.draw.service;

import com.softeer.backend.fo_domain.draw.domain.DrawParticipationInfo;
import com.softeer.backend.fo_domain.draw.dto.main.DrawMainFullAttendResponseDto;
import com.softeer.backend.fo_domain.draw.dto.main.DrawMainResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawLoseModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawWinModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryLoserResponseDto;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryResponseDto;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryWinnerResponseDto;
import com.softeer.backend.fo_domain.draw.exception.DrawException;
import com.softeer.backend.fo_domain.draw.repository.DrawParticipationInfoRepository;
import com.softeer.backend.fo_domain.draw.util.DrawUtil;
import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import com.softeer.backend.fo_domain.share.exception.ShareInfoException;
import com.softeer.backend.fo_domain.share.exception.ShareUrlInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareInfoRepository;
import com.softeer.backend.fo_domain.share.repository.ShareUrlInfoRepository;
import com.softeer.backend.global.annotation.EventLock;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import com.softeer.backend.global.common.response.ResponseDto;
import com.softeer.backend.global.staticresources.util.StaticResourcesUtil;
import com.softeer.backend.global.util.DrawRedisUtil;
import com.softeer.backend.global.util.EventLockRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class DrawService {
    private final DrawParticipationInfoRepository drawParticipationInfoRepository;
    private final ShareInfoRepository shareInfoRepository;
    private final ShareUrlInfoRepository shareUrlInfoRepository;
    private final DrawRedisUtil drawRedisUtil;
    private final StaticResourcesUtil staticResourcesUtil;
    private final DrawUtil drawUtil;
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

        // 초대한 친구 수, 복권 기회 조회
        ShareInfo shareInfo = shareInfoRepository.findShareInfoByUserId(userId)
                .orElseThrow(() -> new ShareInfoException(ErrorStatus._NOT_FOUND));

        int drawParticipationCount = drawParticipationInfo.getDrawParticipationCount();
        int invitedNum = shareInfo.getInvitedNum();
        int remainDrawCount = shareInfo.getRemainDrawCount();

        if (drawParticipationCount == 7) {
            // 7일 연속 출석자라면
            return responseMainFullAttend(invitedNum, remainDrawCount, drawParticipationCount);
        } else {
            // 연속 출석자가 아니라면
            return responseMainNotAttend(invitedNum, remainDrawCount, drawParticipationCount);
        }
    }

    /**
     * 7일 연속 출석 시 상품 정보 모달 만들어서 반환하는 메서드
     *
     * @param invitedNum             초대한 사람 수
     * @param remainDrawCount        남은 추첨 기회
     * @param drawParticipationCount 연속 출석 일수
     * @return 7일 연속 출석 상품 모달
     */
    private DrawMainFullAttendResponseDto responseMainFullAttend(int invitedNum, int remainDrawCount, int drawParticipationCount) {
        return DrawMainFullAttendResponseDto.builder()
                .invitedNum(invitedNum)
                .remainDrawCount(remainDrawCount)
                .drawParticipationCount(drawParticipationCount)
                .fullAttendModal(drawUtil.generateFullAttendModal())
                .build();
    }

    /**
     * 7일 미만 출석 시 모달 만들어서 반환하는 메서드
     *
     * @param invitedNum             초대한 사람 수
     * @param remainDrawCount        남은 추첨 기회
     * @param drawParticipationCount 연속 출석 일수
     * @return 7일 미만 출석 상품 모달
     */
    private DrawMainResponseDto responseMainNotAttend(int invitedNum, int remainDrawCount, int drawParticipationCount) {
        return DrawMainResponseDto.builder()
                .invitedNum(invitedNum)
                .remainDrawCount(remainDrawCount)
                .drawParticipationCount(drawParticipationCount)
                .build();
    }

    /**
     * 추첨 이벤트 당첨 로직 작성
     *
     * @param userId 사용자 아이디
     * @return 추첨 결과에 따른 응답 반환
     */
    public ResponseDto<DrawModalResponseDto> participateDrawEvent(Integer userId) {
        // 복권 기회 조회
        ShareInfo shareInfo = shareInfoRepository.findShareInfoByUserId(userId)
                .orElseThrow(() -> new ShareInfoException(ErrorStatus._NOT_FOUND));

        int remainDrawCount = shareInfo.getRemainDrawCount();

        // 만약 남은 참여 기회가 0이라면
        if (remainDrawCount == 0) {
            return ResponseDto.onSuccess(responseLoseModal(userId));
        }

        // 만약 당첨 목록에 존재한다면 이미 오늘은 한 번 당첨됐다는 뜻이므로 LoseModal 반환
        int ranking = getRankingIfWinner(userId); // 당첨 목록에 존재한다면 랭킹 반환
        if (ranking != 0) {
            shareInfoRepository.decreaseRemainDrawCount(userId); // 횟수 1회 차감
            increaseDrawParticipationCount(); // 추첨 이벤트 참여자수 증가
            drawParticipationInfoRepository.increaseLoseCount(userId);  // 낙첨 횟수 증가
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
            shareInfoRepository.decreaseRemainDrawCount(userId); // 횟수 1회 차감

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
                increaseDrawParticipationCount(); // 추첨 이벤트 참여자수 증가
                drawParticipationInfoRepository.increaseWinCount(userId); // 당첨 횟수 증가
                return ResponseDto.onSuccess(responseWinModal()); // WinModal 반환
            } else {
                // 추첨 티켓이 다 팔렸다면 로직상 당첨자라도 실패 반환
                increaseDrawParticipationCount(); // 추첨 이벤트 참여자수 증가
                drawParticipationInfoRepository.increaseLoseCount(userId);  // 낙첨 횟수 증가
                return ResponseDto.onSuccess(responseLoseModal(userId)); // LoseModal 반환
            }
        } else { // 낙첨자일 경우
            shareInfoRepository.decreaseRemainDrawCount(userId); // 횟수 1회 차감
            increaseDrawParticipationCount(); // 추첨 이벤트 참여자수 증가
            drawParticipationInfoRepository.increaseLoseCount(userId);  // 낙첨 횟수 증가
            return ResponseDto.onSuccess(responseLoseModal(userId)); // LoseModal 반환
        }
    }

    /**
     * 낙첨자 응답 만들어서 반환
     *
     * @param userId 를 이용하여 공유 url 조회
     * @return 낙첨자 응답
     */
    private DrawLoseModalResponseDto responseLoseModal(Integer userId) {
        String shareUrl = getShareUrl(userId);

        return DrawLoseModalResponseDto.builder()
                .isDrawWin(false)
                .images(drawUtil.generateLoseImages())
                .shareUrl(shareUrl)
                .build();
    }

    /**
     * 당첨자 응답 만들어서 반환
     *
     * @return 당첨자 응답
     */
    private DrawWinModalResponseDto responseWinModal() {
        return DrawWinModalResponseDto.builder()
                .isDrawWin(true)
                .images(drawUtil.generateWinImages())
                .winModal(drawUtil.generateWinModal())
                .build();
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
    public ResponseDto<DrawHistoryResponseDto> getDrawHistory(Integer userId) {
        int ranking = getRankingIfWinner(userId);

        if (ranking != 0) {
            // 당첨자라면
            drawUtil.setRanking(ranking);
            return ResponseDto.onSuccess(DrawHistoryWinnerResponseDto.builder()
                    .isDrawWin(true)
                    .winModal(drawUtil.generateWinModalForHistory())
                    .build());
        }

        // 당첨자가 아니라면
        String shareUrl = getShareUrl(userId);
        return ResponseDto.onSuccess(DrawHistoryLoserResponseDto.builder()
                .isDrawWin(false)
                .shareUrl(shareUrl)
                .build());
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

    /**
     * 공유 url 조회
     *
     * @param userId 사용자 아이디
     * @return 공유 url
     */
    private String getShareUrl(Integer userId) {
        return staticResourcesUtil.getData("BASE_URL") + shareUrlInfoRepository.findShareUrlByUserId(userId)
                .orElseThrow(() -> new ShareUrlInfoException(ErrorStatus._NOT_FOUND));
    }
}
