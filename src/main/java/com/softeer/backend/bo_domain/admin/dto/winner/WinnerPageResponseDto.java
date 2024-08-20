package com.softeer.backend.bo_domain.admin.dto.winner;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.softeer.backend.bo_domain.admin.serializer.PercentageSerializer;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import lombok.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class WinnerPageResponseDto {

    public static final int EXPECTED_PARTICIPANT_COUNT = 10000;

    private List<FcfsEvent> fcfsEventList;

    private List<DrawEvent> drawEventList;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FcfsEvent {

        private int round;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate eventDate;

        private int winnerNum;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class DrawEvent {

        private int rank;

        private int winnerNum;

        @JsonSerialize(using = PercentageSerializer.class)
        private double probability;
    }

    public static WinnerPageResponseDto of(List<FcfsSetting> fcfsSettingList, DrawSetting drawSetting) {
        List<FcfsEvent> fcfsEventList = fcfsSettingList.stream()
                .map((fcfsSetting) ->
                        FcfsEvent.builder()
                                .round(fcfsSetting.getRound())
                                .eventDate(LocalDate.from(fcfsSetting.getStartTime()))
                                .winnerNum(fcfsSetting.getWinnerNum())
                                .build())
                .toList();

        DrawEvent drawEvent1 = DrawEvent.builder()
                .rank(1)
                .winnerNum(drawSetting.getWinnerNum1())
                .probability(calculateWinningProbability(drawSetting.getWinnerNum1()))
                .build();
        DrawEvent drawEvent2 = DrawEvent.builder()
                .rank(2)
                .winnerNum(drawSetting.getWinnerNum2())
                .probability(calculateWinningProbability(drawSetting.getWinnerNum2()))
                .build();
        DrawEvent drawEvent3 = DrawEvent.builder()
                .rank(3)
                .winnerNum(drawSetting.getWinnerNum3())
                .probability(calculateWinningProbability(drawSetting.getWinnerNum3()))
                .build();

        List<DrawEvent> drawEventList = Arrays.asList(drawEvent1, drawEvent2, drawEvent3);

        return WinnerPageResponseDto.builder()
                .fcfsEventList(fcfsEventList)
                .drawEventList(drawEventList)
                .build();

    }

    private static double calculateWinningProbability(int winnerNum) {
        return (double) winnerNum / (double) EXPECTED_PARTICIPANT_COUNT;
    }
}
