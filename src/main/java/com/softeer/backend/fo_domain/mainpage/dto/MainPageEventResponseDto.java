package com.softeer.backend.fo_domain.mainpage.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class MainPageEventResponseDto {

    private String eventPeriod;

    private FcfsInfo fcfsInfo;

    private DrawInfo drawInfo;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FcfsInfo{

        private String fcfsInfo;

        private String fcfsTitle;

        private String fcfsContent;

        private String fcfsRewardImage1;

        private String fcfsRewardImage2;

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class DrawInfo{

        private String totalDrawWinner;

        private String remainDrawCount;

        private String drawTitle;

        private String drawContent;

        private String drawRewardImage1;

        private String drawRewardImage2;

        private String drawRewardImage3;
    }

}
