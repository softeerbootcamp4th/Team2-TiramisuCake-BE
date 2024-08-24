package com.softeer.backend.fo_domain.draw.test.lua;

import com.softeer.backend.fo_domain.draw.dto.participate.DrawModalResponseDto;
import com.softeer.backend.fo_domain.draw.repository.DrawParticipationInfoRepository;
import com.softeer.backend.fo_domain.draw.repository.DrawRepository;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.draw.util.DrawAttendanceCountUtil;
import com.softeer.backend.fo_domain.draw.util.DrawResponseGenerateUtil;
import com.softeer.backend.fo_domain.draw.util.DrawUtil;
import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import com.softeer.backend.fo_domain.share.exception.ShareInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareInfoRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.util.DrawRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DrawRedisLuaTest {
    private final DrawParticipationInfoRepository drawParticipationInfoRepository;
    private final ShareInfoRepository shareInfoRepository;
    private final DrawRedisUtil drawRedisUtil;
    private final DrawUtil drawUtil;
    private final DrawResponseGenerateUtil drawResponseGenerateUtil;
    private final DrawAttendanceCountUtil drawAttendanceCountUtil;
    private final DrawSettingManager drawSettingManager;
    private final DrawRepository drawRepository;
    private final DrawRedisLuaUtil drawRedisLuaUtil;

    public DrawModalResponseDto participateDrawEvent(Integer userId) {
        // 복권 기회 조회
        ShareInfo shareInfo = shareInfoRepository.findShareInfoByUserId(userId)
                .orElseThrow(() -> new ShareInfoException(ErrorStatus._NOT_FOUND));

        // 만약 남은 참여 기회가 0이라면
        if (shareInfo.getRemainDrawCount() == 0) {
            return drawResponseGenerateUtil.generateDrawLoserResponse(userId);
        }

        drawRedisLuaUtil.increaseDrawParticipationCount(); // 추첨 이벤트 참여자수 증가
        shareInfoRepository.decreaseRemainDrawCount(userId); // 횟수 1회 차감

        // 만약 당첨 목록에 존재한다면 이미 오늘은 한 번 당첨됐다는 뜻이므로 LoseModal 반환
        int ranking = drawRedisLuaUtil.getRankingIfWinner(userId); // 당첨 목록에 존재한다면 랭킹 반환
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

            if (drawRedisLuaUtil.isWinner(userId, ranking, winnerNum)) { // 레디스에 추첨 티켓이 남았다면, 레디스 당첨 목록에 추가
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
}
