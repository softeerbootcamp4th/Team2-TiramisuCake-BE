package com.softeer.backend.fo_domain.mainpage.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class MainPageCarResponseDto {

    private List<CarInfo> carInfoList;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class CarInfo{

        private int id;

        private String title;

        private String subTitle;

        private String imgTitle;

        private String imgContent;

        private String imgUrl;

        private String backgroundImgUrl;

        private List<CarDetailInfo> carDetailInfoList;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class CarDetailInfo{

        private int id;

        private String title;

        private String subTitle;

        private String content;

        private String imgUrl;
    }
}
