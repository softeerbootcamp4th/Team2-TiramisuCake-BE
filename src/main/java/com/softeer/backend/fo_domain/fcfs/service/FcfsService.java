package com.softeer.backend.fo_domain.fcfs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import com.softeer.backend.fo_domain.fcfs.dto.*;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsFailResult;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResultResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsSuccessResult;
import com.softeer.backend.fo_domain.fcfs.exception.FcfsException;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsRepository;
import com.softeer.backend.fo_domain.fcfs.service.FcfsHandler.FcfsHandler;
import com.softeer.backend.global.annotation.EventLock;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import com.softeer.backend.global.staticresources.constant.S3FileName;
import com.softeer.backend.global.staticresources.constant.StaticTextName;
import com.softeer.backend.global.staticresources.util.StaticResourceUtil;
import com.softeer.backend.global.util.FcfsRedisUtil;
import com.softeer.backend.global.util.RandomCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * 선착순 관련 이벤트를 처리하는 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FcfsService {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M월 d일");
    private final ObjectProvider<FcfsService> fcfsServiceProvider;

    private final FcfsSettingManager fcfsSettingManager;
    private final DrawSettingManager drawSettingManager;
    private final QuizManager quizManager;
    private final FcfsRedisUtil fcfsRedisUtil;
    private final RandomCodeUtil randomCodeUtil;
    private final StaticResourceUtil staticResourceUtil;

    private final FcfsRepository fcfsRepository;
    private final FcfsHandler fcfsHandler;

    /**
     * 선착순 페이지에 필요한 정보를 반환하는 메서드
     */
    public FcfsPageResponseDto getFcfsPage(int round) {

        QuizDto quiz = quizManager.getQuiz(round);
        Map<String, String> textContentMap = staticResourceUtil.getTextContentMap();

        return FcfsPageResponseDto.builder()
                .answerWord(quiz.getAnswerWord())
                .answerSentence(quiz.getAnswerSentence())
                .startIndex(quiz.getStartIndex())
                .endIndex(quiz.getEndIndex())
                .quizDescription(staticResourceUtil.format(textContentMap.get(StaticTextName.FCFS_QUIZ_DESCRIPTION.name()),
                        fcfsSettingManager.getFcfsWinnerNum()))
                .build();
    }

    /**
     * 선착순 튜토리얼 페이지에 필요한 정보를 반환하는 메서드
     */
    public FcfsPageResponseDto getFcfsTutorialPage() {

        QuizDto tutorialQuiz = quizManager.getTutorialQuiz();
        Map<String, String> textContentMap = staticResourceUtil.getTextContentMap();

        return FcfsPageResponseDto.builder()
                .answerWord(tutorialQuiz.getAnswerWord())
                .answerSentence(tutorialQuiz.getAnswerSentence())
                .startIndex(tutorialQuiz.getStartIndex())
                .endIndex(tutorialQuiz.getEndIndex())
                .quizDescription(staticResourceUtil.format(textContentMap.get(StaticTextName.FCFS_QUIZ_DESCRIPTION.name()),
                        fcfsSettingManager.getFcfsWinnerNum()))
                .build();
    }

    public FcfsResultResponseDto handleFcfs(int userId, int round, FcfsRequestDto fcfsRequestDto) throws JsonProcessingException {
        return fcfsHandler.handleFcfsEvent(userId, round, fcfsRequestDto);
    }



    /**
     * 선착순 당첨 기록 응답을 반환하는 메서드
     */
    public FcfsHistoryResponseDto getFcfsHistory(int userId){
        fcfsRepository.findByUserIdOrderByWinningDateAsc(userId);

        Map<String, String> s3ContentMap = staticResourceUtil.getS3ContentMap();

        LocalDate now = LocalDate.now();

        List<Fcfs> fcfsList = fcfsRepository.findByUserIdOrderByWinningDateAsc(userId);
        List<FcfsHistoryResponseDto.FcfsHistory> fcfsHistoryList = new ArrayList<>(fcfsList.stream()
                .map((fcfs) ->
                        FcfsHistoryResponseDto.FcfsHistory.builder()
                                .barcode(s3ContentMap.get(S3FileName.BARCODE_IMAGE.name()))
                                .fcfsCode(fcfs.getCode())
                                .winningDate(fcfs.getWinningDate())
                                .build()
                ).toList());

        Integer round = fcfsSettingManager.getFcfsRoundForHistory(now);
        if(round == null)
            round = fcfsSettingManager.getFcfsRoundForHistory(now.minusDays(1));
        if(round != null
                && fcfsRedisUtil.isValueInIntegerSet(RedisKeyPrefix.FCFS_USERID_PREFIX.getPrefix() + round, userId)){
            Map<String, Integer> fcfsMap = fcfsRedisUtil.getHashEntries(RedisKeyPrefix.FCFS_CODE_USERID_PREFIX.getPrefix() + round);

            for (Map.Entry<String, Integer> entry : fcfsMap.entrySet()) {
                if (entry.getValue().equals(userId)) {
                    String fcfsCode = entry.getKey();

                    fcfsHistoryList.add(FcfsHistoryResponseDto.FcfsHistory.builder()
                            .barcode(s3ContentMap.get(S3FileName.BARCODE_IMAGE.name()))
                            .fcfsCode(fcfsCode)
                            .winningDate(now)
                            .build());

                    break;
                }
            }
        }

        return FcfsHistoryResponseDto.builder()
                .isFcfsWin(!fcfsHistoryList.isEmpty())
                .fcfsHistoryList(fcfsHistoryList)
                .build();

    }

    public void insertFcfsCode(int num, int round){
        fcfsRedisUtil.clearStringSet(RedisKeyPrefix.FCFS_CODE_PREFIX.getPrefix() + round);

        int i=0;
        while(i<num*2){
            fcfsRedisUtil.addToStringSet(RedisKeyPrefix.FCFS_CODE_PREFIX.getPrefix() + round, makeFcfsCode(round));
            i++;
        }
    }

    private String makeFcfsCode(int round) {
        return (char) ('A' + round - 1) + randomCodeUtil.generateRandomCode(5);
    }

}
