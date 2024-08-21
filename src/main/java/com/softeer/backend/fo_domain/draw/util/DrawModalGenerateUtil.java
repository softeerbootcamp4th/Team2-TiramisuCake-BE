package com.softeer.backend.fo_domain.draw.util;

import com.softeer.backend.fo_domain.draw.dto.modal.WinModal;
import com.softeer.backend.global.staticresources.constant.S3FileName;
import com.softeer.backend.global.staticresources.constant.StaticTextName;
import com.softeer.backend.global.staticresources.util.StaticResourceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 당첨 모달 생성하는 클래스
 */
@Component
@RequiredArgsConstructor
public class DrawModalGenerateUtil {

    private final StaticResourceUtil staticResourceUtil;

    /**
     * 등수에 따른 WinModal을 반환
     */
    @Cacheable(value = "staticResources", key = "'draw_modal_' + #ranking")
    public WinModal generateWinModal(int ranking) {

        Map<String, String> textContentMap = staticResourceUtil.getTextContentMap();
        Map<String, String> s3ContentMap = staticResourceUtil.getS3ContentMap();

        if (ranking == 1) {
            return generateFirstWinModal(textContentMap, s3ContentMap);
        } else if (ranking == 2) {
            return generateSecondWinModal(textContentMap, s3ContentMap);
        } else if (ranking == 3) {
            return generateThirdWinModal(textContentMap, s3ContentMap);
        } else {
            return generateFullAttendModal(textContentMap, s3ContentMap);
        }
    }

    /**
     * 1등 WinModal 반환
     */

    private WinModal generateFirstWinModal(Map<String, String> textContentMap, Map<String, String> s3ContentMap) {
        return WinModal.builder()
                .title(textContentMap.get(StaticTextName.DRAW_WINNER_MODAL_TITLE.name()))
                .subtitle(textContentMap.get(StaticTextName.DRAW_FIRST_WINNER_SUBTITLE.name()))
                .img(s3ContentMap.get(S3FileName.DRAW_REWARD_IMAGE_1.name()))
                .description(StaticTextName.DRAW_WINNER_MODAL_DESCRIPTION.name())
                .build();
    }

    /**
     * 2등 WinModal 반환
     */
    private WinModal generateSecondWinModal(Map<String, String> textContentMap, Map<String, String> s3ContentMap) {
        return WinModal.builder()
                .title(textContentMap.get(StaticTextName.DRAW_WINNER_MODAL_TITLE.name()))
                .subtitle(textContentMap.get(StaticTextName.DRAW_SECOND_WINNER_SUBTITLE.name()))
                .img(s3ContentMap.get(S3FileName.DRAW_REWARD_IMAGE_2.name()))
                .description(textContentMap.get(StaticTextName.DRAW_WINNER_MODAL_DESCRIPTION.name()))
                .build();
    }

    /**
     * 3등 WinModal 반환
     */
    private WinModal generateThirdWinModal(Map<String, String> textContentMap, Map<String, String> s3ContentMap) {
        return WinModal.builder()
                .title(textContentMap.get(StaticTextName.DRAW_WINNER_MODAL_TITLE.name()))
                .subtitle(textContentMap.get(StaticTextName.DRAW_THIRD_WINNER_SUBTITLE.name()))
                .img(s3ContentMap.get(S3FileName.DRAW_REWARD_IMAGE_3.name()))
                .description(textContentMap.get(StaticTextName.DRAW_WINNER_MODAL_DESCRIPTION.name()))
                .build();
    }

    /**
     * 7일 연속 출석자 상품 정보 반환
     */
    public WinModal generateFullAttendModal(Map<String, String> textContentMap, Map<String, String> s3ContentMap) {
        return WinModal.builder()
                .title(textContentMap.get(StaticTextName.FULL_ATTEND_MODAL_TITLE.name()))
                .subtitle(textContentMap.get(StaticTextName.FULL_ATTEND_MODAL_SUBTITLE.name()))
                .img(s3ContentMap.get(S3FileName.ATTENDANCE_REWARD_IMAGE.name()))
                .description(textContentMap.get(StaticTextName.FULL_ATTEND_MODAL_DESCRIPTION.name()))
                .build();
    }
}
