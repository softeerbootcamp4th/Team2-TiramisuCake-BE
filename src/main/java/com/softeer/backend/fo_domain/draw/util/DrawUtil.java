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

/**
 * 추첨 이벤트와 관련된 로직을 수행하는 클래스
 */
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
     * 만약 1, 2, 3등 중 하나에 당첨되었다면 등수와 이미지 방향이 결정됨
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
     * 당첨자를 위한 방향 이미지 List 반환
     * 세 개의 이미지가 모두 같은 방향이어야 함
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
     * 낙첨자를 위한 랜덤 방향 이미지 List 반환
     * 세 개의 이미지 중 적어도 하나는 다른 방향이어야 함
     * do-while문을 이용해 방향이 방향 생성
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
     * 방향에 따른 이미지 url list를 반환하는 메서드
     * direction은 방향을 나타냄. 0, 1, 2, 3이 각각 위, 오른쪽, 밑, 왼쪽
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
