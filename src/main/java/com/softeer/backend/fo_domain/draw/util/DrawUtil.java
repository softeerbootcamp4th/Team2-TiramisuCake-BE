package com.softeer.backend.fo_domain.draw.util;

import com.softeer.backend.fo_domain.draw.dto.LoseModal;
import com.softeer.backend.fo_domain.draw.dto.WinModal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrawUtil {
    private boolean isDrawWin = false;
    private int ranking = 0;
    private final int first;
    private final int second;
    private final int third;
    private int directionForWinner;

    public DrawUtil(int first, int second, int third) {
        this.first = first;
        this.second = second;
        this.third = third;
        setRanking();
    }

    /**
     * 등수 지정
     * 만약 1, 2, 3등 중 하나에 당첨되었다면 등수와 이미지 방향이 결정됨.
     */
    private void setRanking() {
        Random random = new Random();
        int randomNum = random.nextInt(10000) + 1; // 랜덤 수

        if (randomNum <= this.first) {
            isDrawWin = true;
            ranking = 1;
            directionForWinner = randomNum % 4;
        } else if (randomNum <= this.second) {
            isDrawWin = true;
            ranking = 2;
            directionForWinner = randomNum % 4;
        } else if (randomNum <= this.third) {
            isDrawWin = true;
            ranking = 3;
            directionForWinner = randomNum % 4;
        }
    }

    public boolean isDrawWin() {
        return this.isDrawWin;
    }

    /**
     *
     * @return 당첨자를 위한 방향 이미지 List 반환
     */
    public List<String> generateWinImages() {
        String directionImage = getImageUrl(directionForWinner);

        ArrayList<String> images = new ArrayList<>(3);
        images.add(directionImage);
        images.add(directionImage);
        images.add(directionImage);
        return images;
    }

    /**
     *
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
     *
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
     *
     * @return 등수에 따른 WinModal을 반환
     */
    public WinModal generateWinModal() {
        if (ranking == 1) {
            return generateFirstWinModal();
        } else if (ranking == 2) {
            return generateSecondWinModal();
        } else {
            return generateThirdWinModal();
        }
    }

    /**
     *
     * @return 1등 WinModal 반환
     */
    private WinModal generateFirstWinModal() {
        return WinModal.builder()
                .title("축하합니다!")
                .subtitle("아이패드 어쩌구")
                .img("image url")
                .description("전화번호 어쩌구")
                .build();
    }

    /**
     *
     * @return 2등 WinModal 반환
     */
    private WinModal generateSecondWinModal() {
        return WinModal.builder()
                .title("축하합니다!")
                .subtitle("현대백화점 10만원권 어쩌구")
                .img("image url")
                .description("전화번호 어쩌구")
                .build();
    }

    /**
     *
     * @return 3등 WinModal 반환
     */
    private WinModal generateThirdWinModal() {
        return WinModal.builder()
                .title("축하합니다!")
                .subtitle("현대백화점 1만원권 어쩌구")
                .img("image url")
                .description("전화번호 어쩌구")
                .build();
    }

    /**
     *
     * @param shareUrl 공유 url
     * @return LoseModal 반환
     */
    public LoseModal generateLoseModal(String shareUrl) {
        return LoseModal.builder()
                .shareUrl(shareUrl)
                .build();
    }
}
