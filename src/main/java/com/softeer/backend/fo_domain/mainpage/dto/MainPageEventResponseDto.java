package com.softeer.backend.fo_domain.mainpage.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class MainPageEventResponseDto {

    private String startDate;

    private String endDate;

    private String eventTitle;

    private String eventDescription;

    private String fcfsInfo;

    private String totalDrawWinner;

    private String remainDrawCount;

    private List<EventInfo> eventInfoList;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class EventInfo{

        private String title;

        private String content;

        private String rewardImage1;

        private String rewardImage2;

    }

}
