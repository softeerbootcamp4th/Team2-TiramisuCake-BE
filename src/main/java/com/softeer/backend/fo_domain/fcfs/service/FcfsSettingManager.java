package com.softeer.backend.fo_domain.fcfs.service;

import com.softeer.backend.bo_domain.eventparticipation.repository.EventParticipationRepository;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.domain.Quiz;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsSettingDto;
import com.softeer.backend.fo_domain.fcfs.dto.QuizDto;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import com.softeer.backend.fo_domain.fcfs.repository.QuizRepository;
import com.softeer.backend.global.util.EventLockRedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 선착순 이벤트 정보를 관리하는 클래스
 */
@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class FcfsSettingManager {

    private final FcfsSettingRepository fcfsSettingRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final EventLockRedisUtil eventLockRedisUtil;
    private final EventParticipationRepository eventParticipationRepository;
    private final QuizRepository quizRepository;

    private List<FcfsSettingDto> fcfsSettingList;
    private QuizDto tutorialQuiz;
    private List<QuizDto> quizList;


    @Setter
    private boolean isFcfsClosed = false;


    @PostConstruct
    public void init() {
        loadInitialData();
    }

    public FcfsSettingDto getFcfsSettingByRound(int round) {
        return fcfsSettingList.get(round - 1);
    }

    /**
     * round 1에 해당하는 선착순 이벤트 속성으로 초기화
     */
    public void loadInitialData() {

        List<FcfsSetting> fcfsSettings = fcfsSettingRepository.findAll();
        fcfsSettingList = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            fcfsSettingList.add(null);  // 인덱스 0부터 3까지 빈 슬롯을 추가
        }

        fcfsSettings.forEach((fcfsSetting) -> {
            fcfsSettingList.set(fcfsSetting.getRound() - 1, FcfsSettingDto.builder()
                    .round(fcfsSetting.getRound())
                    .startTime(fcfsSetting.getStartTime())
                    .endTime(fcfsSetting.getEndTime())
                    .winnerNum(fcfsSetting.getWinnerNum())
                    .build());
        });

        List<Quiz> quizs = quizRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        quizList = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            quizList.add(null);  // 인덱스 0부터 3까지 빈 슬롯을 추가
        }

        quizs.forEach((quiz) -> {

            QuizDto quizDto = QuizDto.builder()
                    .hint(quiz.getHint())
                    .answerWord(quiz.getAnswerWord())
                    .answerSentence(quiz.getAnswerSentence())
                    .startIndex(quiz.getStartIndex())
                    .endIndex(quiz.getEndIndex())
                    .build();

            if(quiz.getHint().equals("튜토리얼"))
                tutorialQuiz = quizDto;
            else
                quizList.add(quizDto);
        });


    }

    public void setFcfsTime(List<FcfsSetting> fcfsSettingList) {
        fcfsSettingList
                .forEach((fcfsSetting) -> {
                    FcfsSettingDto fcfsSettingDto = this.fcfsSettingList.get(fcfsSetting.getRound()-1);
                    fcfsSettingDto.setStartTime(fcfsSetting.getStartTime());
                    fcfsSettingDto.setEndTime(fcfsSetting.getEndTime());
                });
    }

    public void setFcfsWinnerNum(int fcfsWinnerNum) {
        fcfsSettingList.forEach((fcfsSettingDto) -> {
            fcfsSettingDto.setWinnerNum(fcfsWinnerNum);
        });
    }

    public int getRoundForScheduler(LocalDate localDate) {
        for (FcfsSettingDto fcfsSettingDto : fcfsSettingList) {
            if (fcfsSettingDto != null) {
                LocalDate startDate = fcfsSettingDto.getStartTime().toLocalDate();
                LocalDate dayAfterStartDate = startDate.plusDays(1);

                // localDate가 startDate의 하루 다음날과 같은지 확인
                if (localDate.equals(dayAfterStartDate)) {
                    return fcfsSettingDto.getRound();
                }
            }
        }
        return -1;  // 해당하는 데이터가 없는 경우
    }

    public int getFcfsWinnerNum(){
        return fcfsSettingList.get(0).getWinnerNum();
    }

    public String getHint(){

        LocalDateTime now = LocalDateTime.now();

        for (int i=0; i<fcfsSettingList.size(); i++) {

            FcfsSettingDto fcfsSettingDto = fcfsSettingList.get(i);

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

        return quizList.get(round - 1);
    }

    public boolean isFcfsEntryAvailable(LocalDateTime now){
        for(FcfsSettingDto fcfsSettingDto : fcfsSettingList){
            LocalDateTime startTime = fcfsSettingDto.getStartTime();
            LocalDateTime endTime = fcfsSettingDto.getEndTime();

            if((now.isEqual(startTime) || now.isAfter(startTime))
                    && (now.isEqual(endTime) || now.isBefore(endTime))) {
                return true;
            }
        }
        return false;
    }

    public Integer getFcfsRound(LocalDateTime now){

        for(FcfsSettingDto fcfsSettingDto : fcfsSettingList){
            LocalDateTime startTime = fcfsSettingDto.getStartTime();
            LocalDateTime endTime = fcfsSettingDto.getEndTime();

            if((now.isEqual(startTime) || now.isAfter(startTime))
                    && (now.isEqual(endTime) || now.isBefore(endTime))) {
                return fcfsSettingDto.getRound();
            }
        }
        return null;
    }


}
