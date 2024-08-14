package com.softeer.backend.fo_domain.fcfs.service;

import com.softeer.backend.fo_domain.fcfs.dto.*;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsFailResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResponseDto;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsRepository;
import com.softeer.backend.fo_domain.user.repository.UserRepository;
import com.softeer.backend.global.staticresources.util.StaticResourcesUtil;
import com.softeer.backend.global.util.EventLockRedisUtil;
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
    private final FcfsRepository fcfsRepository;
    private final EventLockRedisUtil eventLockRedisUtil;
    private final UserRepository userRepository;
    private final StaticResourcesUtil staticResourcesUtil;


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
//    public FcfsResponseDto handleFcfsEvent(int userId, int round, String answer) {
//        if (fcfsSettingManager.isFcfsClosed())
//            return countFcfsParticipant(fcfsSettingManager.getRound());
//
//        return saveFcfsWinners(userId, fcfsSettingManager.getRound());
//    }

    /**
     * 1. Redisson lock을 걸고 선착순 이벤트 참여자 수가 지정된 수보다 적다면, 선착순 당첨 정보를 DB에 저장하고
     * Redis에 저장된 선착순 이벤트 참여자 수를 1만큼 증가시키도 선착순 당첨 응답을 생성하여 반환한다.
     * 만약, 참여자 수가 총 당첨자 수와 같아졌으면, fcfsSettingManager의 setFcfsClosed를 true로 변환한다.
     * 2. setFcfsClosed가 true로 바뀌게 전에 요청이 들어왔다면, 선착순 실패 응답을 생성하여 반환한다.
     */
//    @EventLock(key = "FCFS_WINNER_#{#round}")
//    private FcfsResponseDto saveFcfsWinners(int userId, int round) {
//        Set<Integer> participantIds = eventLockRedisUtil.getAllDataAsSet(RedisLockPrefix.FCFS_LOCK_PREFIX.getPrefix() + round);
//
//        if (participantIds.size() < fcfsSettingManager.getWinnerNum() &&
//                !eventLockRedisUtil.isParticipantExists(RedisLockPrefix.FCFS_LOCK_PREFIX.getPrefix() + round, userId)) {
//            User user = userRepository.findById(userId)
//                    .orElseThrow(() -> {
//                        log.error("user not found in saveFcfsWinners method.");
//                        return new UserException(ErrorStatus._NOT_FOUND);
//                    });
//
//            Fcfs fcfs = Fcfs.builder()
//                    .user(user)
//                    .round(round)
//                    .build();
//            fcfsRepository.save(fcfs);
//
//            eventLockRedisUtil.incrementParticipantCount(RedisLockPrefix.FCFS_PARTICIPANT_COUNT_PREFIX.getPrefix() + round);
//            if (participantIds.size() + 1 == fcfsSettingManager.getWinnerNum()) {
//                fcfsSettingManager.setFcfsClosed(true);
//            }
//
//            return new FcfsSuccessResponseDto(1);
//        }
//
//        return new FcfsFailResponseDtoDto(1);
//    }

    public FcfsResponseDto getFcfsResult(boolean fcfsWin){
//        if(fcfsWin){
//            return FcfsSuccessResponseDto.builder()
//                    .title(staticResourcesUtil.getData("FCFS_WINNER_TITLE"))
//                    .subTitle(staticResourcesUtil.getData("FCFS_WINNER_SUBTITLE"))
//                    .qrCode(staticResourcesUtil.getData("barcode_image"))
//                    .codeWord(staticResourcesUtil.getData("FCFS_WINNER_CODE_WORD"))
//                    .fcfsCode()
//                    .expirationDate(staticResourcesUtil.getData("FCFS_WINNER_EXPIRY_DATE"))
//                    .caution(staticResourcesUtil.getData("FCFS_WINNER_CAUTION"))
//                    .build();
//        }

        return FcfsFailResponseDto.builder()
                .title(staticResourcesUtil.getData("FCFS_LOSER_TITLE"))
                .subTitle(staticResourcesUtil.getData("FCFS_LOSER_SUBTITLE"))
                .caution(staticResourcesUtil.getData("FCFS_LOSER_CAUTION"))
                .build();
    }

}
