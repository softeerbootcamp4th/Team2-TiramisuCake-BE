package com.softeer.backend.fo_domain.fcfs.dto;

import lombok.*;

/**
 * 선착순 페이지 퀴즈 응답 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
public class FcfsPageResponseDto {

    private String answerWord;

    private String answerSentence;

    private int startIndex;

    private int endIndex;

    private String quizDescription;

}
