package com.softeer.backend.fo_domain.draw.service;

import com.softeer.backend.fo_domain.draw.domain.Draw;
import com.softeer.backend.fo_domain.draw.domain.DrawParticipationInfo;
import com.softeer.backend.fo_domain.draw.dto.history.DrawHistoryDto;
import com.softeer.backend.fo_domain.draw.dto.main.DrawMainResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.history.DrawHistoryResponseDto;
import com.softeer.backend.fo_domain.draw.exception.DrawException;
import com.softeer.backend.fo_domain.draw.repository.DrawParticipationInfoRepository;
import com.softeer.backend.fo_domain.draw.repository.DrawRepository;
import com.softeer.backend.fo_domain.draw.test.tablelock.DrawDatabaseTest;
import com.softeer.backend.fo_domain.draw.test.tablelock.DrawDatabaseUtil;
import com.softeer.backend.fo_domain.draw.util.DrawAttendanceCountUtil;
import com.softeer.backend.fo_domain.draw.util.DrawResponseGenerateUtil;
import com.softeer.backend.fo_domain.draw.util.DrawUtil;
import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import com.softeer.backend.fo_domain.share.exception.ShareInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareInfoRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.util.DrawRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 추첨 참여 로직을 처리하기 위한 클래스
 */
@Service
@RequiredArgsConstructor
public class DrawService {
    private final DrawParticipationInfoRepository drawParticipationInfoRepository;
    private final ShareInfoRepository shareInfoRepository;
    private final DrawRedisUtil drawRedisUtil;
    private final DrawUtil drawUtil;
    private final DrawResponseGenerateUtil drawResponseGenerateUtil;
    private final DrawAttendanceCountUtil drawAttendanceCountUtil;
    private final DrawSettingManager drawSettingManager;
    private final DrawDatabaseUtil drawDatabaseUtil;
    private final DrawDatabaseTest drawDatabaseTest;
    private final DrawRepository drawRepository;

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

        int drawAttendanceCount = drawParticipationInfo.getDrawAttendanceCount();

        if (drawAttendanceCount != 7) {
            drawAttendanceCount = drawAttendanceCountUtil.handleAttendanceCount(userId, drawParticipationInfo);
        }
        int invitedNum = shareInfo.getInvitedNum();
        int remainDrawCount = shareInfo.getRemainDrawCount();

        System.out.println("Draw Attendance = " + drawAttendanceCount);

        if (drawAttendanceCount >= 7) {
            // 7일 연속 출석자라면
            return drawResponseGenerateUtil.generateMainFullAttendResponse(invitedNum, remainDrawCount, drawAttendanceCount % 8);
        } else {
            // 연속 출석자가 아니라면
            return drawResponseGenerateUtil.generateMainNotAttendResponse(invitedNum, remainDrawCount, drawAttendanceCount);
        }
    }

    /**
     * 추첨 이벤트 참여를 위한 메서드
     *
     * 1. 남은 참여 기회가 0이라면 실패 응답 반환하고 종료
     * 2. 추첨 이벤트 참여자 수 증가
     * 3. 해당 사용자의 추첨 이벤트 참여 기회 1회 차감
     * 4. 오늘 이미 당첨된 사용자인지 확인
     *  4-1. 이미 당첨된 사용자라면 사용자의 낙첨 횟수 1회 증가, 낙첨 응답 반환
     * 5. 추첨 이벤트 설정으로부터 각 등수의 당첨자 수 조회
     * 6. 추첨 로직 실행
     *  6-1. 당첨자일 경우
     *   6-1-1. 레디스에 해당 등수의 자리가 남았을 경우: 레디스에 사용자 넣기, 해당 사용자의 당첨 횟수 증가, 당첨 응답 반환
     *   6-1-2. 레디스에 해당 등수의 자리가 없을 경우 해당 사용자의 낙첨 횟수 증가, 낙첨 응답 반환
     *  6-2. 낙첨자일 경우 해당 사용자의 낙첨 횟수 증가, 낙첨 응답 반환
     */
    public DrawModalResponseDto participateDrawEvent(Integer userId) {
        return drawDatabaseTest.participateDrawEvent(userId);
//        // 복권 기회 조회
//        ShareInfo shareInfo = shareInfoRepository.findShareInfoByUserId(userId)
//                .orElseThrow(() -> new ShareInfoException(ErrorStatus._NOT_FOUND));
//
//        // 만약 남은 참여 기회가 0이라면
//        if (shareInfo.getRemainDrawCount() == 0) {
//            return drawResponseGenerateUtil.generateDrawLoserResponse(userId);
//        }
//
//        drawRedisUtil.increaseDrawParticipationCount(); // 추첨 이벤트 참여자수 증가
//        shareInfoRepository.decreaseRemainDrawCount(userId); // 횟수 1회 차감
//
//        // 만약 당첨 목록에 존재한다면 이미 오늘은 한 번 당첨됐다는 뜻이므로 LoseModal 반환
//        int ranking = drawRedisUtil.getRankingIfWinner(userId); // 당첨 목록에 존재한다면 랭킹 반환
//        if (ranking != 0) {
//            drawParticipationInfoRepository.increaseLoseCount(userId);  // 낙첨 횟수 증가
//            return drawResponseGenerateUtil.generateDrawLoserResponse(userId); // LoseModal 반환
//        }
//
//        // 당첨자 수 조회
//        int first = drawSettingManager.getWinnerNum1(); // 1등 수
//        int second = drawSettingManager.getWinnerNum2(); // 2등 수
//        int third = drawSettingManager.getWinnerNum3(); // 3등 수
//
//        // 당첨자 수 설정
//        drawUtil.setFirst(first);
//        drawUtil.setSecond(second);
//        drawUtil.setThird(third);
//
//        // 추첨 로직 실행
//        drawUtil.performDraw();
//
//        if (drawUtil.isDrawWin()) { // 당첨자일 경우
//            ranking = drawUtil.getRanking();
//            int winnerNum;
//            if (ranking == 1) {
//                winnerNum = first;
//            } else if (ranking == 2) {
//                winnerNum = second;
//            } else {
//                winnerNum = third;
//            }
//
//            if (drawRedisUtil.isWinner(userId, ranking, winnerNum)) { // 레디스에 추첨 티켓이 남았다면, 레디스 당첨 목록에 추가
//                // 추첨 티켓이 다 팔리지 않았다면
//                drawParticipationInfoRepository.increaseWinCount(userId); // 당첨 횟수 증가
//                return drawResponseGenerateUtil.generateDrawWinnerResponse(ranking); // WinModal 반환
//            } else {
//                // 추첨 티켓이 다 팔렸다면 로직상 당첨자라도 실패 반환
//                drawParticipationInfoRepository.increaseLoseCount(userId);  // 낙첨 횟수 증가
//                return drawResponseGenerateUtil.generateDrawLoserResponse(userId); // LoseModal 반환
//            }
//        } else { // 낙첨자일 경우
//            drawParticipationInfoRepository.increaseLoseCount(userId);  // 낙첨 횟수 증가
//            return drawResponseGenerateUtil.generateDrawLoserResponse(userId); // LoseModal 반환
//        }
    }

    /**
     * 당첨 내역 조회하는 메서드
     * 1. DB 조회
     * 2. redis 조회
     * 3. 내역을 리스트로 만들어서 반환
     * 3-1. 내역이 없다면 내역이 없다는 응답 반환
     *
     * @param userId 사용자 아이디
     * @return 당첨 내역에 따른 응답
     */
    public DrawHistoryResponseDto getDrawHistory(Integer userId) {
        int ranking = drawRedisUtil.getRankingIfWinner(userId);
        List<Draw> drawList = drawRepository.findAllByUserIdOrderByWinningDateAsc(userId);
        List<DrawHistoryDto> drawHistoryList = new ArrayList<>();

        // DB내역을 리스트로 만들기
        for (Draw draw : drawList) {
            int drawRank = draw.getRank();
            drawHistoryList.add(DrawHistoryDto.builder()
                    .drawRank(drawRank)
                    .winningDate(draw.getWinningDate())
                    .image(drawResponseGenerateUtil.getImageUrl(drawRank))
                    .build());
        }

        // redis 내역을 리스트로 만들기
        if (ranking != 0) {
            drawHistoryList.add(DrawHistoryDto.builder()
                    .drawRank(ranking)
                    .winningDate(LocalDate.now())
                    .image(drawResponseGenerateUtil.getImageUrl(ranking))
                    .build());
        }

        if (!drawHistoryList.isEmpty()) {
            // 당첨자라면
            return drawResponseGenerateUtil.generateDrawHistoryWinnerResponse(drawHistoryList);
        }

        // 당첨자가 아니라면
        return drawResponseGenerateUtil.generateDrawHistoryLoserResponse(userId);
    }
}
