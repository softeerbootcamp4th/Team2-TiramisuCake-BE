package com.softeer.backend.fo_domain.draw.util;

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
        Random random = new Random();
        int firstDirection, secondDirection, thirdDirection;

        do {
            firstDirection = random.nextInt(4);
            secondDirection = random.nextInt(4);
            thirdDirection = random.nextInt(4);
        } while (firstDirection == secondDirection && secondDirection == thirdDirection);

        images.add(getImageUrl(firstDirection));
        images.add(getImageUrl(secondDirection));
        images.add(getImageUrl(thirdDirection));
        return images;
    }

    /**
     * @param direction 방향을 나타냄. 0, 1, 2, 3이 각각 위, 오른쪽, 밑, 왼쪽
     * @return 방향에 따른 이미지 url을 반환
     */
    private String getImageUrl(int direction) {
        String directionImage;
        if (direction == 0) {
            directionImage = staticResourcesUtil.getData("draw_block_up_image");
        } else if (direction == 1) {
            directionImage = staticResourcesUtil.getData("draw_block_right_image");
        } else if (direction == 2) {
            directionImage = staticResourcesUtil.getData("draw_block_down_image");
        } else {
            directionImage = staticResourcesUtil.getData("draw_block_left_image");
        }
        return directionImage;
    }
}
