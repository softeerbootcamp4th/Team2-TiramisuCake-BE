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

    public static WinnerPageResponseDto of(FcfsSettingManager fcfsSettingManager, DrawSettingManager drawSettingManager) {
        List<FcfsEvent> fcfsEventList = fcfsSettingManager.getFcfsSettingList().stream()
                .map((fcfsSettingDto) ->
                        FcfsEvent.builder()
                                .round(fcfsSettingDto.getRound())
                                .eventDate(LocalDate.from(fcfsSettingDto.getStartTime()))
                                .winnerNum(fcfsSettingDto.getWinnerNum())
                                .build())
                .toList();

        DrawEvent drawEvent1 = DrawEvent.builder()
                .rank(1)
                .winnerNum(drawSettingManager.getWinnerNum1())
                .probability(calculateWinningProbability(drawSettingManager.getWinnerNum1()))
                .build();
        DrawEvent drawEvent2 = DrawEvent.builder()
                .rank(2)
                .winnerNum(drawSettingManager.getWinnerNum2())
                .probability(calculateWinningProbability(drawSettingManager.getWinnerNum2()))
                .build();
        DrawEvent drawEvent3 = DrawEvent.builder()
                .rank(3)
                .winnerNum(drawSettingManager.getWinnerNum3())
                .probability(calculateWinningProbability(drawSettingManager.getWinnerNum3()))
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
