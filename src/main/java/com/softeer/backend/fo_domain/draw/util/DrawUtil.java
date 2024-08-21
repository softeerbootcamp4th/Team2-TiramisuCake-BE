package com.softeer.backend.fo_domain.draw.util;

import com.softeer.backend.global.staticresources.constant.S3FileName;
import com.softeer.backend.global.staticresources.util.StaticResourceUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DrawUtil {

    private final ObjectProvider<DrawUtil> drawUtilProvider;
    private final StaticResourceUtil staticResourceUtil;

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

        DrawUtil drawUtil = drawUtilProvider.getObject();
        String directionImage = drawUtil.getImageUrl(direction);

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
        DrawUtil drawUtil = drawUtilProvider.getObject();

        ArrayList<String> images = new ArrayList<>(3);
        Random random = new Random();
        int firstDirection, secondDirection, thirdDirection;

        do {
            firstDirection = random.nextInt(4);
            secondDirection = random.nextInt(4);
            thirdDirection = random.nextInt(4);
        } while (firstDirection == secondDirection && secondDirection == thirdDirection);

        images.add(drawUtil.getImageUrl(firstDirection));
        images.add(drawUtil.getImageUrl(secondDirection));
        images.add(drawUtil.getImageUrl(thirdDirection));
        return images;
    }

    /**
     * @param direction 방향을 나타냄. 0, 1, 2, 3이 각각 위, 오른쪽, 밑, 왼쪽
     * @return 방향에 따른 이미지 url을 반환
     */
    @Cacheable(value = "staticResources", key = "'drawImage_' + #direction")
    public String getImageUrl(int direction) {

        Map<String, String> s3ContentMap = staticResourceUtil.getS3ContentMap();

        String directionImage;
        if (direction == 0) {
            directionImage = s3ContentMap.get(S3FileName.DRAW_BLOCK_UP_IMAGE.name());
        } else if (direction == 1) {
            directionImage = s3ContentMap.get(S3FileName.DRAW_BLOCK_RIGHT_IMAGE.name());
        } else if (direction == 2) {
            directionImage = s3ContentMap.get(S3FileName.DRAW_BLOCK_DOWN_IMAGE.name());
        } else {
            directionImage = s3ContentMap.get(S3FileName.DRAW_BLOCK_LEFT_IMAGE.name());
        }
        return directionImage;
    }
}
