package com.softeer.backend.fo_domain.fcfs.service;

import com.softeer.backend.fo_domain.fcfs.dto.*;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsFailResult;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResult;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResultResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsSuccessResult;
import com.softeer.backend.fo_domain.fcfs.exception.FcfsException;
import com.softeer.backend.global.annotation.EventLock;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import com.softeer.backend.global.staticresources.util.StaticResourcesUtil;
import com.softeer.backend.global.util.FcfsRedisUtil;
import com.softeer.backend.global.util.RandomCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 선착순 관련 이벤트를 처리하는 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FcfsService {

    private final FcfsSettingManager fcfsSettingManager;
    private final FcfsRedisUtil fcfsRedisUtil;
    private final StaticResourcesUtil staticResourcesUtil;
    private final RandomCodeUtil randomCodeUtil;


    public FcfsPageResponseDto getFcfsPage(int round) {

        QuizDto quiz = fcfsSettingManager.getQuiz(round);

        return FcfsPageResponseDto.builder()
                .answerWord(quiz.getAnswerWord())
                .answerSentence(quiz.getAnswerSentence())
                .startIndex(quiz.getStartIndex())
                .endIndex(quiz.getEndIndex())
                .quizDescription(staticResourcesUtil.getData("FCFS_QUIZ_DESCRIPTION"))
                .build();
    }

    public FcfsPageResponseDto getFcfsTutorialPage() {
        QuizDto tutorialQuiz = fcfsSettingManager.getTutorialQuiz();

        return FcfsPageResponseDto.builder()
                .answerWord(tutorialQuiz.getAnswerWord())
                .answerSentence(tutorialQuiz.getAnswerSentence())
                .startIndex(tutorialQuiz.getStartIndex())
                .endIndex(tutorialQuiz.getEndIndex())
                .quizDescription(staticResourcesUtil.getData("FCFS_QUIZ_DESCRIPTION"))
                .build();
    }

    /**
     * 1. 선착순 당첨자가 아직 다 결정되지 않았으면, 선착순 당첨 응답 생성 및 반환
     * 2. 선착순 당첨자가 다 결정됐다면, Redisson lock을 사용하지 않고 Redis에 저장된 선착순 이벤트 참여자 수를 1명씩 더한다.
     */
    public String handleFcfsEvent(int userId, int round, FcfsRequestDto fcfsRequestDto) {

        if(!fcfsRequestDto.getAnswer().equals(fcfsSettingManager.getQuiz(round).getAnswerWord())) {
            log.error("fcfs quiz answer is not match, correct answer: {}, wrong anwer: {}",
                    fcfsSettingManager.getQuiz(round).getAnswerWord(), fcfsRequestDto.getAnswer());
            throw new FcfsException(ErrorStatus._BAD_REQUEST);
        }

        if (fcfsSettingManager.isFcfsClosed()){
            countFcfsParticipant(round);

            return null;
        }

        return saveFcfsWinners(userId, round);
    }

    @EventLock(key = "FCFS_WINNER_#{#round}")
    private String saveFcfsWinners(int userId, int round) {

        long numOfWinners = fcfsRedisUtil.getIntegerSetSize(RedisKeyPrefix.FCFS_LOCK_PREFIX.getPrefix() + round);



            // redis에 userId 등록
            fcfsRedisUtil.addToIntegerSet(RedisKeyPrefix.FCFS_LOCK_PREFIX.getPrefix() + round, userId);

            // redis에 code 등록
            String code = makeFcfsCode(round);
            while(fcfsRedisUtil.isValueInStringSet(RedisKeyPrefix.FCFS_CODE_PREFIX.getPrefix() + round, code)){
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

            return code;


    }

    private String makeFcfsCode(int round){
        return (char)('A'+round-1) + randomCodeUtil.generateRandomCode(5);
    }

    private void countFcfsParticipant(int round) {
        fcfsRedisUtil.incrementValue(RedisKeyPrefix.FCFS_PARTICIPANT_COUNT_PREFIX.getPrefix() + round);
    }

    public FcfsResultResponseDto getFcfsResult(boolean fcfsWin, String fcfsCode){
        if(fcfsWin){
            FcfsSuccessResult fcfsSuccessResult = FcfsSuccessResult.builder()
                    .title(staticResourcesUtil.getData("FCFS_WINNER_TITLE"))
                    .subTitle(staticResourcesUtil.getData("FCFS_WINNER_SUBTITLE"))
                    .qrCode(staticResourcesUtil.getData("barcode_image"))
                    .codeWord(staticResourcesUtil.getData("FCFS_WINNER_CODE_WORD"))
                    .fcfsCode(fcfsCode)
                    .expirationDate(staticResourcesUtil.getData("FCFS_WINNER_EXPIRY_DATE"))
                    .caution(staticResourcesUtil.getData("FCFS_WINNER_CAUTION"))
                    .build();

            return FcfsResultResponseDto.builder()
                    .isFcfsWinner(fcfsWin)
                    .fcfsResult(fcfsSuccessResult)
                    .build();
        }

        FcfsFailResult fcfsFailResult = FcfsFailResult.builder()
                .title(staticResourcesUtil.getData("FCFS_LOSER_TITLE"))
                .subTitle(staticResourcesUtil.getData("FCFS_LOSER_SUBTITLE"))
                .caution(staticResourcesUtil.getData("FCFS_LOSER_CAUTION"))
                .build();

        return FcfsResultResponseDto.builder()
                .isFcfsWinner(fcfsWin)
                .fcfsResult(fcfsFailResult)
                .build();
    }



}
