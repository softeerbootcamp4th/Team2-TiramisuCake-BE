package com.softeer.backend.bo_domain.admin.dto.winner;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.softeer.backend.bo_domain.admin.serializer.PhoneNumberSerializer;
import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class FcfsWinnerListResponseDto {

    int round;

    private List<FcfsWinner> fcfsWinnerList;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FcfsWinner {

        private String name;

        @JsonSerialize(using = PhoneNumberSerializer.class)
        private String phoneNumber;
    }

    public static FcfsWinnerListResponseDto of(List<Fcfs> fcfsList, int round) {
        List<FcfsWinner> fcfsWinnerList = fcfsList.stream()
                .map((fcfs) -> FcfsWinner.builder()
                        .name(fcfs.getUser().getName())
                        .phoneNumber(fcfs.getUser().getPhoneNumber())
                        .build())
                .sorted(Comparator.comparing(FcfsWinner::getName))
                .toList();

        return FcfsWinnerListResponseDto.builder()
                .round(round)
                .fcfsWinnerList(fcfsWinnerList)
                .build();
    }
}
