package com.softeer.backend.bo_domain.admin.dto.indicator;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.softeer.backend.bo_domain.admin.serializer.PercentageSerializer;
import com.softeer.backend.bo_domain.admin.serializer.PhoneNumberSerializer;
import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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

    public static EventIndicatorResponseDto of(List<EventParticipation> eventParticipationList) {
        LocalDate startDate = eventParticipationList.get(0).getEventDate();
        LocalDate endDate = eventParticipationList.get(eventParticipationList.size() - 1).getEventDate();

        int totalVisitorCount = eventParticipationList.stream()
                .mapToInt(EventParticipation::getVisitorCount)
                .sum();

        int totalFcfsParticipantCount = eventParticipationList.stream()
                .mapToInt(EventParticipation::getFcfsParticipantCount)
                .sum();

        int totalDrawParticipantCount = eventParticipationList.stream()
                .mapToInt(EventParticipation::getDrawParticipantCount)
                .sum();

        double fcfsParticipantRate = (double) totalFcfsParticipantCount / (double) totalVisitorCount;
        double drawParticipantRate = (double) totalDrawParticipantCount / (double) totalVisitorCount;

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
