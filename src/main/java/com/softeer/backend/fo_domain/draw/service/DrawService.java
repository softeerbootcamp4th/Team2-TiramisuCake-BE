package com.softeer.backend.fo_domain.draw.service;

import com.softeer.backend.fo_domain.draw.domain.DrawParticipationInfo;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.dto.DrawResponseDto;
import com.softeer.backend.fo_domain.draw.exception.DrawException;
import com.softeer.backend.fo_domain.draw.repository.DrawParticipationInfoRepository;
import com.softeer.backend.fo_domain.draw.repository.DrawRepository;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import com.softeer.backend.fo_domain.share.exception.ShareInfoException;
import com.softeer.backend.fo_domain.share.repository.ShareInfoRepository;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class DrawService {
    private final DrawRepository drawRepository;
    private final DrawParticipationInfoRepository drawParticipationInfoRepository;
    private final ShareInfoRepository shareInfoRepository;
    private final DrawSettingRepository drawSettingRepository;

    public ResponseDto<DrawResponseDto> getDrawMainPageInfo(Integer userId) {
        // 참여 정보 (연속참여일수) 조회
        DrawParticipationInfo drawParticipationInfo = drawParticipationInfoRepository.findDrawParticipationInfoByUserId(userId)
                .orElseThrow(() -> new DrawException(ErrorStatus._DRAW_PARTICIPATION_INFO_NOT_FOUND));

        // 초대한 친구 수, 복권 기회 조회
        ShareInfo shareInfo = shareInfoRepository.findShareInfoByUserId(userId)
                .orElseThrow(() -> new ShareInfoException(ErrorStatus._SHARE_INFO_NOT_FOUND));

        // 추첨 게임 설정 정보 가져오기
        DrawSetting drawSetting = drawSettingRepository.findById(1)
                .orElseThrow(() -> new DrawException(ErrorStatus._DRAW_PARTICIPATION_INFO_NOT_FOUND));

        // 등수 결정하기
        int first = drawSetting.getWinnerNum1(); // 1등 수
        int second = drawSetting.getWinnerNum2(); // 2등 수
        int third = drawSetting.getWinnerNum3(); // 3등 수

        Random random = new Random();
        int randomNum = random.nextInt(10000) + 1; // 랜덤 수

        boolean isDrawWin = false;
        int ranking = 0;
        if (randomNum <= first) {
            isDrawWin = true;
            ranking = 1;
        } else if (randomNum <= second) {
            isDrawWin = true;
            ranking = 2;
        } else if (randomNum <= third) {
            isDrawWin = true;
            ranking = 3;
        }

        int drawParticipationCount = drawParticipationInfo.getDrawParticipationCount();
        int invitedNum = shareInfo.getInvitedNum();
        int remainDrawCount = shareInfo.getRemainDrawCount();

        return ResponseDto.onSuccess(DrawResponseDto.builder()
                .invitedNum(invitedNum)
                .remainDrawCount(remainDrawCount)
                .drawParticipationCount(drawParticipationCount)
                .isDrawWin(isDrawWin)
                .build());
    }
}
