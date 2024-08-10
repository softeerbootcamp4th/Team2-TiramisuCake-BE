package com.softeer.backend.bo_domain.admin.dto.main;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.softeer.backend.bo_domain.admin.serializer.PercentageSerializer;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
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
public class MainPageResponseDto {
    public static final int EXPECTED_PARTICIPANT_COUNT = 10000;

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

        private int winnerNum;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class DrawEvent {

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        @JsonFormat(pattern = "hh:mm:ss")
        private LocalTime startTime;

        @JsonFormat(pattern = "hh:mm:ss")
        private LocalTime endTime;

        private List<DrawInfo> drawInfoList;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class DrawInfo {

        private int rank;

        private int winnerNum;

        @JsonSerialize(using = PercentageSerializer.class)
        private double probability;
    }

    public static MainPageResponseDto of(List<FcfsSetting> fcfsSettingList, List<DrawSetting> drawSettingList) {
        List<FcfsEvent> fcfsEventList = fcfsSettingList.stream()
                .map((fcfsSetting) ->
                        FcfsEvent.builder()
                                .round(fcfsSetting.getRound())
                                .startTime(fcfsSetting.getStartTime())
                                .endTime(fcfsSetting.getEndTime())
                                .winnerNum(fcfsSetting.getWinnerNum())
                                .build())
                .toList();

        DrawSetting drawSetting = drawSettingList.get(0);
        DrawInfo drawInfoFirst = DrawInfo.builder()
                .rank(1)
                .winnerNum(drawSetting.getWinnerNum1())
                .probability(calculateWinningProbability(drawSetting.getWinnerNum1()))
                .build();
        DrawInfo drawInfoSecond = DrawInfo.builder()
                .rank(2)
                .winnerNum(drawSetting.getWinnerNum2())
                .probability(calculateWinningProbability(drawSetting.getWinnerNum2()))
                .build();
        DrawInfo drawInfoThird = DrawInfo.builder()
                .rank(3)
                .winnerNum(drawSetting.getWinnerNum3())
                .probability(calculateWinningProbability(drawSetting.getWinnerNum3()))
                .build();

        List<DrawInfo> drawInfoList = Arrays.asList(drawInfoFirst, drawInfoSecond, drawInfoThird);
        DrawEvent drawEvent = DrawEvent.builder()
                .startDate(drawSetting.getStartDate())
                .endDate(drawSetting.getEndDate())
                .startTime(drawSetting.getStartTime())
                .endTime(drawSetting.getEndTime())
                .drawInfoList(drawInfoList)
                .build();

        return MainPageResponseDto.builder()
                .fcfsEventList(fcfsEventList)
                .drawEvent(drawEvent)
                .build();

    }

    private static double calculateWinningProbability(int winnerNum) {
        return (double) winnerNum / (double) EXPECTED_PARTICIPANT_COUNT;
    }
}
