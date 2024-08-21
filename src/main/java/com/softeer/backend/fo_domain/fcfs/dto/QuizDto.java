package com.softeer.backend.fo_domain.fcfs.dto;

import jakarta.persistence.Column;
import lombok.*;

/**
 * 선착순 퀴즈 정보 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
public class QuizDto {

    private String hint;

    private String answerWord;

    private String answerSentence;

    private int startIndex;

    private int endIndex;

}
