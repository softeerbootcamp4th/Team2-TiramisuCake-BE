package com.softeer.backend.bo_domain.admin.dto.winner;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.softeer.backend.bo_domain.admin.serializer.PhoneNumberSerializer;
import com.softeer.backend.fo_domain.draw.domain.Draw;
import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import lombok.*;

import java.util.Comparator;
import java.util.List;

/**
 * 추첨 당첨자 목록 응답 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class DrawWinnerListResponseDto {

    int rank;

    private List<DrawWinner> drawWinnerList;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class DrawWinner {

        private String name;

        @JsonSerialize(using = PhoneNumberSerializer.class)
        private String phoneNumber;
    }

    public static DrawWinnerListResponseDto of(List<Draw> drawList, int rank) {
        List<DrawWinner> drawWinnerList = drawList.stream()
                .map((draw) -> DrawWinner.builder()
                        .name(draw.getUser().getName())
                        .phoneNumber(draw.getUser().getPhoneNumber())
                        .build())
                .sorted(Comparator.comparing(DrawWinnerListResponseDto.DrawWinner::getName))
                .toList();

        return DrawWinnerListResponseDto.builder()
                .rank(rank)
                .drawWinnerList(drawWinnerList)
                .build();
    }

}
