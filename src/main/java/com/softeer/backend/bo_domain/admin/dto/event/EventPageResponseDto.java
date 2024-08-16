package com.softeer.backend.bo_domain.admin.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.softeer.backend.bo_domain.admin.dto.main.AdminMainPageResponseDto;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class EventPageResponseDto {

    private List<FcfsEvent> fcfsEventList;

    private DrawEvent drawEvent;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FcfsEvent {

        private int round;

        @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
        private LocalDateTime startTime;

        @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
        private LocalDateTime endTime;

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class DrawEvent {

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime startTime;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime endTime;

    }

    public static EventPageResponseDto of(FcfsSettingManager fcfsSettingManager, DrawSettingManager drawSettingManager) {
        List<FcfsEvent> fcfsEventList = fcfsSettingManager.getFcfsSettingList().stream()
                .map((fcfsSettingDto) ->
                        EventPageResponseDto.FcfsEvent.builder()
                                .round(fcfsSettingDto.getRound())
                                .startTime(fcfsSettingDto.getStartTime())
                                .endTime(fcfsSettingDto.getEndTime())
                                .build())
                .toList();

        DrawEvent drawEvent = DrawEvent.builder()
                .startDate(drawSettingManager.getStartDate())
                .endDate(drawSettingManager.getEndDate())
                .startTime(drawSettingManager.getStartTime())
                .endTime(drawSettingManager.getEndTime())
                .build();

        return EventPageResponseDto.builder()
                .fcfsEventList(fcfsEventList)
                .drawEvent(drawEvent)
                .build();

    }
}
