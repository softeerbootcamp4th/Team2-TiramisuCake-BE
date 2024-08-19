package com.softeer.backend.fo_domain.mainpage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    public static class EventInfo{

        private String title;

        private String content;

        private String rewardImage1;

        private String rewardImage2;

    }

}
