package com.softeer.backend.bo_domain.admin.dto.indicator;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.softeer.backend.bo_domain.admin.serializer.PercentageSerializer;
import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 이벤트 지표 조회 응답 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class EventIndicatorResponseDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private int totalVisitorCount;

    private int totalFcfsParticipantCount;

    private int totalDrawParticipantCount;

    @JsonSerialize(using = PercentageSerializer.class)
    private double fcfsParticipantRate;

    @JsonSerialize(using = PercentageSerializer.class)
    private double drawParticipantRate;

    private List<VisitorNum> visitorNumList;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class VisitorNum {

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate visitDate;

        private int visitorNum;
    }

    public static EventIndicatorResponseDto of(List<EventParticipation> eventParticipationList, DrawSetting drawSetting) {
        LocalDate startDate = drawSetting.getStartDate();
        LocalDate endDate = drawSetting.getEndDate();

        int totalVisitorCount = eventParticipationList.stream()
                .mapToInt(EventParticipation::getVisitorCount)
                .sum();

        int totalFcfsParticipantCount = eventParticipationList.stream()
                .mapToInt(EventParticipation::getFcfsParticipantCount)
                .sum();

        int totalDrawParticipantCount = eventParticipationList.stream()
                .mapToInt(EventParticipation::getDrawParticipantCount)
                .sum();

        double fcfsParticipantRate = totalVisitorCount == 0 ? 0 : (double) totalFcfsParticipantCount / (double) totalVisitorCount;
        double drawParticipantRate = totalVisitorCount == 0 ? 0 : (double) totalDrawParticipantCount / (double) totalVisitorCount;

        List<VisitorNum> visitorNumList = eventParticipationList.stream()
                .map((eventParticipation) ->
                        VisitorNum.builder()
                                .visitDate(eventParticipation.getEventDate())
                                .visitorNum(eventParticipation.getVisitorCount())
                                .build())
                .toList();

        return EventIndicatorResponseDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalVisitorCount(totalVisitorCount)
                .totalFcfsParticipantCount(totalFcfsParticipantCount)
                .totalDrawParticipantCount(totalDrawParticipantCount)
                .fcfsParticipantRate(fcfsParticipantRate)
                .drawParticipantRate(drawParticipantRate)
                .visitorNumList(visitorNumList)
                .build();
    }
}
