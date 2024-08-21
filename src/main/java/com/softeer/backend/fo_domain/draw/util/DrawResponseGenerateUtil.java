package com.softeer.backend.fo_domain.draw.util;

import com.softeer.backend.fo_domain.draw.dto.main.DrawMainFullAttendResponseDto;
import com.softeer.backend.fo_domain.draw.dto.main.DrawMainResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawLoseModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.participate.DrawWinModalResponseDto;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryLoserResponseDto;
import com.softeer.backend.fo_domain.draw.dto.result.DrawHistoryWinnerResponseDto;
import com.softeer.backend.fo_domain.share.exception.ShareUrlInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareUrlInfoRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 추첨 이벤트 결과 응답을 생성하는 클래스
 */
@Component
@RequiredArgsConstructor
public class DrawResponseGenerateUtil {
    // 공유 url의 기본 url
    public static final String BASE_URL = "https://softeer.site/share/";

    private final ShareUrlInfoRepository shareUrlInfoRepository;
    private final DrawUtil drawUtil;
    private final DrawModalGenerateUtil drawModalGenerateUtil;


    /**
     * 7일 연속 출석 시 상품 정보 모달 만들어서 반환하는 메서드
     *
     * @param invitedNum             초대한 사람 수
     * @param remainDrawCount        남은 추첨 기회
     * @param drawAttendanceCount 연속 출석 일수
     * @return 7일 연속 출석 상품 모달
     */
    public DrawMainFullAttendResponseDto generateMainFullAttendResponse(int invitedNum, int remainDrawCount, int drawAttendanceCount) {
        return DrawMainFullAttendResponseDto.builder()
                .invitedNum(invitedNum)
                .remainDrawCount(remainDrawCount)
                .drawAttendanceCount(drawAttendanceCount)
                .fullAttendModal(drawModalGenerateUtil.generateWinModal(7))
                .build();
    }

    /**
     * 7일 미만 출석 시 모달 만들어서 반환하는 메서드
     *
     * @param invitedNum             초대한 사람 수
     * @param remainDrawCount        남은 추첨 기회
     * @param drawAttendanceCount 연속 출석 일수
     * @return 7일 미만 출석 상품 모달
     */
    public DrawMainResponseDto generateMainNotAttendResponse(int invitedNum, int remainDrawCount, int drawAttendanceCount) {
        return DrawMainResponseDto.builder()
                .invitedNum(invitedNum)
                .remainDrawCount(remainDrawCount)
                .drawAttendanceCount(drawAttendanceCount)
                .build();
    }

    /**
     * 낙첨자 응답 만들어서 반환
     *
     * @param userId 를 이용하여 공유 url 조회
     * @return 낙첨자 응답
     */
    public DrawLoseModalResponseDto generateDrawLoserResponse(Integer userId) {
        return DrawLoseModalResponseDto.builder()
                .isDrawWin(false)
                .images(drawUtil.generateLoseImages())
                .shareUrl(getShareUrl(userId))
                .build();
    }

    /**
     * 등수에 따른 당첨자 응답 만들어서 반환
     *
     * @param ranking 등수
     * @return 당첨자 응답
     */
    public DrawWinModalResponseDto generateDrawWinnerResponse(int ranking) {
        return DrawWinModalResponseDto.builder()
                .isDrawWin(true)
                .images(drawUtil.generateWinImages())
                .winModal(drawModalGenerateUtil.generateWinModal(ranking))
                .build();
    }

    /**
     * 당첨내역이 있는 경우 당첨 내역 응답 만들어서 반환
     *
     * @param ranking 등수
     * @return 당첨 내역 응답
     */
    public DrawHistoryWinnerResponseDto generateDrawHistoryWinnerResponse(int ranking) {
        return DrawHistoryWinnerResponseDto.builder()
                .isDrawWin(true)
                .winModal(drawModalGenerateUtil.generateWinModal(ranking))
                .build();
    }

    /**
     * 당첨내역이 없는 경우 낙첨 응답 만들어서 반환
     *
     * @param userId 사용자 아이디
     * @return 낙첨 내역 응답
     */
    public DrawHistoryLoserResponseDto generateDrawHistoryLoserResponse(Integer userId) {
        return DrawHistoryLoserResponseDto.builder()
                .isDrawWin(false)
                .shareUrl(getShareUrl(userId))
                .build();
    }

    /**
     * 공유 url 조회
     *
     * @param userId 사용자 아이디
     * @return 공유 url
     */
    private String getShareUrl(Integer userId) {
        return BASE_URL + shareUrlInfoRepository.findShareUrlByUserId(userId)
                .orElseThrow(() -> new ShareUrlInfoException(ErrorStatus._NOT_FOUND));
    }
}
