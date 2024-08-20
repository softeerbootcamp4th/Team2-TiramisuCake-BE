package com.softeer.backend.fo_domain.fcfs.service;

import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.domain.Quiz;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsSettingDto;
import com.softeer.backend.fo_domain.fcfs.dto.QuizDto;
import com.softeer.backend.fo_domain.fcfs.repository.QuizRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
public class QuizManager {

    private final FcfsSettingManager fcfsSettingManager;
    private final QuizRepository quizRepository;

    private QuizDto tutorialQuiz;
    private List<QuizDto> quizList;

    @PostConstruct
    public void init() {
        loadInitialData();
    }

    public void loadInitialData() {

        List<Quiz> quizs = quizRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        quizList = new ArrayList<>();

        quizs.forEach((quiz) -> {

            QuizDto quizDto = QuizDto.builder()
                    .hint(quiz.getHint())
                    .answerWord(quiz.getAnswerWord())
                    .answerSentence(quiz.getAnswerSentence().replace("\\n", "\n"))
                    .startIndex(quiz.getStartIndex())
                    .endIndex(quiz.getEndIndex())
                    .build();

            if(quiz.getHint().equals("튜토리얼"))
                tutorialQuiz = quizDto;
            else
                quizList.add(quizDto);
        });
    }

    public String getHint(){

        LocalDateTime now = LocalDateTime.now();

        for (int i=0; i<fcfsSettingManager.getFcfsSettingList().size(); i++) {

            FcfsSettingDto fcfsSettingDto = fcfsSettingManager.getFcfsSettingList().get(i);

            if (fcfsSettingDto != null) {
                LocalDateTime endTime = fcfsSettingDto.getEndTime();

                // localDate가 startDate의 하루 다음날과 같은지 확인
                if (endTime.isBefore(now)) {
                    return quizList.get(i).getHint();
                }
            }
        }

        return null;
    }

    public QuizDto getQuiz(int round){
        log.info("quiz: {}", quizList.get(round-1));
        return quizList.get(round - 1);
    }
}
