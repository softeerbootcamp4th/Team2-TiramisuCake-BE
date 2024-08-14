package com.softeer.backend.fo_domain.fcfs.dto;

import jakarta.persistence.Column;
import lombok.*;

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
