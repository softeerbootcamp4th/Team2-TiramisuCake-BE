package com.softeer.backend.fo_domain.fcfs.service;

import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.dto.*;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsFailResult;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResultResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsSuccessResult;
import com.softeer.backend.fo_domain.fcfs.exception.FcfsException;
import com.softeer.backend.global.annotation.EventLock;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import com.softeer.backend.global.staticresources.constant.S3FileName;
import com.softeer.backend.global.staticresources.constant.StaticTextName;
import com.softeer.backend.global.staticresources.domain.S3Content;
import com.softeer.backend.global.staticresources.domain.TextContent;
import com.softeer.backend.global.staticresources.repository.S3ContentRepository;
import com.softeer.backend.global.staticresources.repository.TextContentRepository;
import com.softeer.backend.global.staticresources.util.StaticResourceUtil;
import com.softeer.backend.global.util.FcfsRedisUtil;
import com.softeer.backend.global.util.RandomCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 1. 선착순 당첨자가 아직 다 결정되지 않았으면, 선착순 당첨 응답 생성 및 반환
     * 2. 선착순 당첨자가 다 결정됐다면, Redisson lock을 사용하지 않고 Redis에 저장된 선착순 이벤트 참여자 수를 1명씩 더한다.
     */
    public FcfsResultResponseDto handleFcfsEvent(int userId, int round, FcfsRequestDto fcfsRequestDto) {

        if(!fcfsRequestDto.getAnswer().equals(quizManager.getQuiz(round).getAnswerWord())) {
            log.error("fcfs quiz answer is not match, correct answer: {}, wrong anwer: {}",
                    quizManager.getQuiz(round).getAnswerWord(), fcfsRequestDto.getAnswer());
            throw new FcfsException(ErrorStatus._BAD_REQUEST);
        }

        if (fcfsSettingManager.isFcfsClosed()){
            countFcfsParticipant(round);

            return getFcfsResult(false, null);
        }
        FcfsService fcfsService = fcfsServiceProvider.getObject();
        return fcfsService.saveFcfsWinners(userId, round);
    }

    @EventLock(key = "FCFS_#{#round}")
    public FcfsResultResponseDto saveFcfsWinners(int userId, int round) {

        long numOfWinners = fcfsRedisUtil.getIntegerSetSize(RedisKeyPrefix.FCFS_USERID_PREFIX.getPrefix() + round);

        if (numOfWinners < fcfsSettingManager.getFcfsWinnerNum()
                && !fcfsRedisUtil.isValueInIntegerSet(RedisKeyPrefix.FCFS_USERID_PREFIX.getPrefix() + round, userId)) {

            // redis에 userId 등록
            fcfsRedisUtil.addToIntegerSet(RedisKeyPrefix.FCFS_USERID_PREFIX.getPrefix() + round, userId);

            // redis에 code 등록
            String code = makeFcfsCode(round);
            while (fcfsRedisUtil.isValueInStringSet(RedisKeyPrefix.FCFS_CODE_PREFIX.getPrefix() + round, code)) {
                code = makeFcfsCode(round);
            }
            fcfsRedisUtil.addToStringSet(RedisKeyPrefix.FCFS_CODE_PREFIX.getPrefix() + round, code);

            // redis에 code-userId 형태로 등록(hash)
            fcfsRedisUtil.addToHash(RedisKeyPrefix.FCFS_CODE_USERID_PREFIX.getPrefix() + round, code, userId);

            // redis에 선착순 참가자 수 +1
            countFcfsParticipant(round);

            // 선착순 당첨이 마감되면 FcfsSettingManager의 fcfsClodes 변수값을 true로 설정
            if (numOfWinners + 1 == fcfsSettingManager.getFcfsWinnerNum()) {
                fcfsSettingManager.setFcfsClosed(true);
            }

            return getFcfsResult(true, code);
        }

        return getFcfsResult(false, null);

    }

    private String makeFcfsCode(int round){
        return (char)('A'+round-1) + randomCodeUtil.generateRandomCode(5);
    }

    private void countFcfsParticipant(int round) {
        fcfsRedisUtil.incrementValue(RedisKeyPrefix.FCFS_PARTICIPANT_COUNT_PREFIX.getPrefix() + round);
    }

    public FcfsResultResponseDto getFcfsResult(boolean fcfsWin, String fcfsCode){

        Map<String, String> textContentMap = staticResourceUtil.getTextContentMap();
        Map<String, String> s3ContentMap = staticResourceUtil.getS3ContentMap();

        FcfsSettingDto firstFcfsSetting = fcfsSettingManager.getFcfsSettingByRound(1);

        FcfsService fcfsService = fcfsServiceProvider.getObject();

        if(fcfsWin){
            FcfsSuccessResult fcfsSuccessResult = fcfsService.getFcfsSuccessResult(
                    textContentMap, s3ContentMap, firstFcfsSetting
            );
            fcfsSuccessResult.setFcfsCode(fcfsCode);

            return FcfsResultResponseDto.builder()
                    .isFcfsWinner(fcfsWin)
                    .fcfsResult(fcfsSuccessResult)
                    .build();
        }

        FcfsFailResult fcfsFailResult = fcfsService.getFcfsFailResult(textContentMap);

        return FcfsResultResponseDto.builder()
                .isFcfsWinner(fcfsWin)
                .fcfsResult(fcfsFailResult)
                .build();
    }

    @Cacheable(value = "staticResources", key = "'fcfsSuccess'")
    public FcfsSuccessResult getFcfsSuccessResult(Map<String, String> textContentMap, Map<String, String> s3ContentMap,
                                                  FcfsSettingDto firstFcfsSetting){

        return FcfsSuccessResult.builder()
                .title(staticResourceUtil.format(textContentMap.get(StaticTextName.FCFS_WINNER_TITLE.name()),
                        fcfsSettingManager.getFcfsWinnerNum()))
                .subTitle(textContentMap.get(StaticTextName.FCFS_WINNER_SUBTITLE.name()))
                .qrCode(s3ContentMap.get(S3FileName.BARCODE_IMAGE.name()))
                .codeWord(textContentMap.get(StaticTextName.FCFS_WINNER_CODE_WORD.name()))
                .expirationDate(staticResourceUtil.format(textContentMap.get(StaticTextName.FCFS_WINNER_EXPIRY_DATE.name()),
                        firstFcfsSetting.getStartTime().getYear(),
                        firstFcfsSetting.getStartTime().format(dateFormatter),
                        drawSettingManager.getEndDate().plusDays(14).format(dateFormatter)))
                .caution(textContentMap.get(StaticTextName.FCFS_WINNER_CAUTION.name()))
                .build();
    }

    @Cacheable(value = "staticResources", key = "'fcfsFail'")
    public FcfsFailResult getFcfsFailResult(Map<String, String> textContentMap){

        return FcfsFailResult.builder()
                .title(textContentMap.get(StaticTextName.FCFS_LOSER_TITLE.name()))
                .subTitle(textContentMap.get(StaticTextName.FCFS_LOSER_SUBTITLE.name()))
                .caution(textContentMap.get(StaticTextName.FCFS_LOSER_CAUTION.name()))
                .build();
    }

}
