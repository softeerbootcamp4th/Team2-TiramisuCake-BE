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


    public List<String> generateWinImages() {
        String directionImage;
        if (directionForWinner == 0) {
            directionImage = "up";
        } else if (directionForWinner == 1) {
            directionImage = "right";
        } else if (directionForWinner == 2) {
            directionImage = "down";
        } else {
            directionImage = "left";
        }

        ArrayList<String> images = new ArrayList<>(3);
        images.add(directionImage);
        images.add(directionImage);
        images.add(directionImage);
        return images;
    }

    public List<String> generateLoseImages() {
        ArrayList<String> images = new ArrayList<>(3);
        images.add("left");
        images.add("right");
        images.add("up");
        return images;
    }

    public WinModal generateWinModal() {
        if (ranking == 1) {
            return generateFirstWinModal();
        } else if (ranking == 2) {
            return generateSecondWinModal();
        } else {
            return generateThirdWinModal();
        }
    }

    private WinModal generateFirstWinModal() {
        return WinModal.builder()
                .title("축하합니다!")
                .subtitle("아이패드 어쩌구")
                .img("image url")
                .description("전화번호 어쩌구")
                .build();
    }

    private WinModal generateSecondWinModal() {
        return WinModal.builder()
                .title("축하합니다!")
                .subtitle("현대백화점 10만원권 어쩌구")
                .img("image url")
                .description("전화번호 어쩌구")
                .build();
    }

    private WinModal generateThirdWinModal() {
        return WinModal.builder()
                .title("축하합니다!")
                .subtitle("현대백화점 1만원권 어쩌구")
                .img("image url")
                .description("전화번호 어쩌구")
                .build();
    }

    public LoseModal generateLoseModal(String shareUrl) {
        return LoseModal.builder()
                .shareUrl(shareUrl)
                .build();
    }
}
