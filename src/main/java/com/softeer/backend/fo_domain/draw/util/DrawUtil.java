package com.softeer.backend.fo_domain.draw.util;

import com.softeer.backend.fo_domain.draw.dto.*;
import com.softeer.backend.global.staticresources.util.StaticResourcesUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DrawUtil {
    private final StaticResourcesUtil staticResourcesUtil;
    @Getter
    private boolean isDrawWin = false;
    @Getter
    @Setter
    private int ranking = 0;
    @Setter
    private int first;
    @Setter
    private int second;
    @Setter
    private int third;

    /**
     * 추첨 로직 실행
     * 만약 1, 2, 3등 중 하나에 당첨되었다면 등수와 이미지 방향이 결정됨.
     */
    public void performDraw() {
        Random random = new Random();
        int randomNum = random.nextInt(10000) + 1; // 랜덤 수

        if (randomNum <= this.first) {
            isDrawWin = true;
            ranking = 1;
        } else if (randomNum <= this.second) {
            isDrawWin = true;
            ranking = 2;
        } else if (randomNum <= this.third) {
            isDrawWin = true;
            ranking = 3;
        }
    }

    /**
     * @return 당첨자를 위한 방향 이미지 List 반환
     */
    public List<String> generateWinImages() {
        Random random = new Random();
        int direction = random.nextInt(4); // 랜덤 수

        String directionImage = getImageUrl(direction);

        ArrayList<String> images = new ArrayList<>(3);
        images.add(directionImage);
        images.add(directionImage);
        images.add(directionImage);
        return images;
    }

    /**
     * @return 낙첨자를 위한 랜덤 방향 이미지 List 반환
     */
    public List<String> generateLoseImages() {
        ArrayList<String> images = new ArrayList<>(3);
        images.add("left");
        images.add("right");
        images.add("up");
        return images;
    }

    /**
     * @param direction 방향을 나타냄. 0, 1, 2, 3이 각각 위, 오른쪽, 밑, 왼쪽
     * @return 방향에 따른 이미지 url을 반환
     */
    private String getImageUrl(int direction) {
        String directionImage;
        if (direction == 0) {
            directionImage = "up";
        } else if (direction == 1) {
            directionImage = "right";
        } else if (direction == 2) {
            directionImage = "down";
        } else {
            directionImage = "left";
        }
        return directionImage;
    }

    /**
     * @return 등수에 따른 WinModal을 반환
     */
    public DrawWinModalResponseDto generateWinModal() {
        if (ranking == 1) {
            return generateFirstWinModal();
        } else if (ranking == 2) {
            return generateSecondWinModal();
        } else {
            return generateThirdWinModal();
        }
    }

    /**
     * @return 1등 WinModal 반환
     */
    private DrawWinModalResponseDto generateFirstWinModal() {
        return DrawWinModalResponseDto.builder()
                .title("축하합니다!")
                .subtitle("아이패드 어쩌구")
                .img("image url")
                .description("전화번호 어쩌구")
                .build();
    }

    /**
     * @return 2등 WinModal 반환
     */
    private DrawWinModalResponseDto generateSecondWinModal() {
        return DrawWinModalResponseDto.builder()
                .title("축하합니다!")
                .subtitle("현대백화점 10만원권 어쩌구")
                .img("image url")
                .description("전화번호 어쩌구")
                .build();
    }

    /**
     * @return 3등 WinModal 반환
     */
    private DrawWinModalResponseDto generateThirdWinModal() {
        return DrawWinModalResponseDto.builder()
                .title("축하합니다!")
                .subtitle("현대백화점 1만원권 어쩌구")
                .img("image url")
                .description("전화번호 어쩌구")
                .build();
    }

    /**
     * @param shareUrl 공유 url
     * @return LoseModal 반환
     */
    public DrawLoseResponseDto.LoseModal generateLoseModal(String shareUrl) {
        return DrawLoseResponseDto.LoseModal.builder()
                .shareUrl(shareUrl)
                .build();
    }

    /**
     * 7일 연속 출석자 상품 정보 반환 메서드
     *
     * @return FullAttendModal 반환
     */
    public DrawMainFullAttendResponseDto.FullAttendModal generateWinFullAttendModal() {
        return DrawMainFullAttendResponseDto.FullAttendModal.builder()
                .title(staticResourcesUtil.getData("FULL_ATTEND_MODAL_TITLE"))
                .subtitle(staticResourcesUtil.getData("FULL_ATTEND_MODAL_SUBTITLE"))
                .image(staticResourcesUtil.getData("attendance_reward_image"))
                .description(staticResourcesUtil.getData("FULL_ATTEND_MODAL_DESCRIPTION"))
                .build();
    }

    /**
     * 7일 연속 출석자 상품 정보 반환 메서드
     *
     * @return FullAttendModal 반환
     */
    public DrawLoseFullAttendResponseDto.FullAttendModal generateLoseFullAttendModal() {
        return DrawLoseFullAttendResponseDto.FullAttendModal.builder()
                .title(staticResourcesUtil.getData("FULL_ATTEND_MODAL_TITLE"))
                .subtitle(staticResourcesUtil.getData("FULL_ATTEND_MODAL_SUBTITLE"))
                .image(staticResourcesUtil.getData("attendance_reward_image"))
                .description(staticResourcesUtil.getData("FULL_ATTEND_MODAL_DESCRIPTION"))
                .build();
    }

    public DrawMainFullAttendResponseDto.FullAttendModal generateFullAttendModal() {
        return DrawMainFullAttendResponseDto.FullAttendModal.builder()
                .title(staticResourcesUtil.getData("FULL_ATTEND_MODAL_TITLE"))
                .subtitle(staticResourcesUtil.getData("FULL_ATTEND_MODAL_SUBTITLE"))
                .image(staticResourcesUtil.getData("attendance_reward_image"))
                .description(staticResourcesUtil.getData("FULL_ATTEND_MODAL_DESCRIPTION"))
                .build();
    }
}
