package com.softeer.backend.fo_domain.mainpage.dto;

import lombok.*;

import java.util.List;

/**
 * 메인 페이지 이벤트 정적 정보를 응답하는 DTO 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class MainPageEventStaticResponseDto {

    private String eventTitle;

    private String eventDescription;

    private List<EventInfo> eventInfoList;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class EventInfo {

        private String title;

        private String content;

        private String rewardName1;

        private String rewardName2;

        private String rewardName3;

        private String rewardImage1;

        private String rewardImage2;

        private String rewardImage3;

    }

}
