package com.softeer.backend.fo_domain.fcfs.service;

import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import com.softeer.backend.fo_domain.fcfs.dto.*;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsFailResult;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResultResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsSuccessResult;
import com.softeer.backend.fo_domain.fcfs.exception.FcfsException;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsRepository;
import com.softeer.backend.fo_domain.fcfs.util.LuaRedisUtil;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 선착순 관련 이벤트를 처리하는 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FcfsService {
    public final static String FCFS_CLOSED = "FCFS_CLOSED";
    public final static String FCFS_CODE_EMPTY = "FCFS_CODE_EMPTY";
    public final static String DUPLICATED = "DUPLICATED";
    public final static String _CLOSED = "_CLOSED";

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M월 d일");
    private final ObjectProvider<FcfsService> fcfsServiceProvider;

    private final FcfsSettingManager fcfsSettingManager;
    private final DrawSettingManager drawSettingManager;
    private final QuizManager quizManager;
    private final FcfsRedisUtil fcfsRedisUtil;
    private final RandomCodeUtil randomCodeUtil;
    private final StaticResourceUtil staticResourceUtil;

    private final FcfsRepository fcfsRepository;
    private final LuaRedisUtil luaRedisUtil;

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

    /**
     * 선착순 등록을 처리하고 결과 모달 정보를 반환하는 메서드
     * <p>
     * 1. 선착순 등록 요청 dto에서 전달된 퀴즈 정답이 유효한지 확인한다.
     * 1-1. 유효하지 않다면 예외가 발생한다.
     * 2. 선착순 설정 매니저 클래스의 fcfsClosed 변수값을 확인한다.(선착순 당첨자가 다 나왔는지 여부를 의미)
     * 2-1. 값이 true라면 선착순 이벤트 참여자 수에 1을 더하고 실패 모달 정보를 반환한다.
     * 2-2. 값이 false라면 선착순 등록을 처리하는 메서드를 호출한다.
     */
    @Transactional(readOnly = true)
    public FcfsResultResponseDto handleFcfsEvent(int userId, int round, FcfsRequestDto fcfsRequestDto) {

        // 퀴즈 정답이 유효한지 확인하고 유효하지 않다면 예외 발생
        if (!fcfsRequestDto.getAnswer().equals(quizManager.getQuiz(round).getAnswerWord())) {
            log.error("fcfs quiz answer is not match, correct answer: {}, wrong anwer: {}",
                    quizManager.getQuiz(round).getAnswerWord(), fcfsRequestDto.getAnswer());
            throw new FcfsException(ErrorStatus._BAD_REQUEST);
        }

        // 선착순 당첨자가 다 나왔다면 선착순 이벤트 참여자 수에 1을 더하는 메서드를 호출하고 실패 모달 정보를 반환
        if (fcfsSettingManager.isFcfsClosed()) {
            countFcfsParticipant(round);

            return getFcfsResult(false, false, null);
        }

        // 선착순 등록을 처리하는 메서드 호출
        FcfsService fcfsService = fcfsServiceProvider.getObject();
        return fcfsService.saveFcfsWinners(userId, round);
    }

    /**
     * 선착순 등록을 처리하는 메서드
     * <p>
     * 1. 'FCFS_CLOSED', "FCFS_CODE_EMPTY" 가 반환되면 실패 모달을 반환한다.
     * 2. 'DUPLICATED' 가 반환되면 중복으로 인한 실패 모달을 반환한다.
     * 3. '_CLOSED' 가 code값에 있다면 fcfsSettingManager의 fcfsClosed 변수값을 true로 설정한다.
     * 4. 선착순 당첨 성공 모달을 반환한다.
     */
    public FcfsResultResponseDto saveFcfsWinners(int userId, int round) {

        int maxWinners = fcfsSettingManager.getFcfsWinnerNum();

        String userIdSetKey = RedisKeyPrefix.FCFS_USERID_PREFIX.getPrefix() + round;
        String codeSetKey = RedisKeyPrefix.FCFS_CODE_PREFIX.getPrefix() + round;
        String codeUserIdHashKey = RedisKeyPrefix.FCFS_CODE_USERID_PREFIX.getPrefix() + round;
        String participantCountKey = RedisKeyPrefix.FCFS_PARTICIPANT_COUNT_PREFIX.getPrefix() + round;

        String result = luaRedisUtil.executeFcfsScript(
                userIdSetKey,
                codeSetKey,
                codeUserIdHashKey,
                participantCountKey,
                userId,
                maxWinners);

        switch (result) {
            case FCFS_CLOSED:
            case FCFS_CODE_EMPTY:

                return getFcfsResult(false, false, null);

            case DUPLICATED:

                return getFcfsResult(false, true, null);

            default:
                String code = result;
                if (result.contains(_CLOSED)) {
                    fcfsSettingManager.setFcfsClosed(true);
                    code = result.replace(_CLOSED, "");
                }

                return getFcfsResult(true, false, code);
        }

    }


    /**
     * redis에 저장된 선착순 이벤트 참여자 수를 1만큼 늘리는 메서드
     */
    private void countFcfsParticipant(int round) {
        fcfsRedisUtil.incrementValue(RedisKeyPrefix.FCFS_PARTICIPANT_COUNT_PREFIX.getPrefix() + round);
    }

    /**
     * 선착순 결과 모달 응답 Dto를 만들어서 반환하는 메서드
     */
    public FcfsResultResponseDto getFcfsResult(boolean fcfsWin, boolean isDuplicated, String fcfsCode) {

        FcfsSettingDto firstFcfsSetting = fcfsSettingManager.getFcfsSettingByRound(1);

        FcfsService fcfsService = fcfsServiceProvider.getObject();

        if (fcfsWin) {
            FcfsSuccessResult fcfsSuccessResult = fcfsService.getFcfsSuccessResult(firstFcfsSetting);
            fcfsSuccessResult.setFcfsCode(fcfsCode);

            return FcfsResultResponseDto.builder()
                    .fcfsWinner(fcfsWin)
                    .fcfsResult(fcfsSuccessResult)
                    .build();
        }

        FcfsFailResult fcfsFailResult = fcfsService.getFcfsFailResult(isDuplicated);

        return FcfsResultResponseDto.builder()
                .fcfsWinner(fcfsWin)
                .fcfsResult(fcfsFailResult)
                .build();
    }

    /**
     * 선착순 당첨 모달 정보 중, 정적 정보를 반환하는 메서드
     */
    @Cacheable(value = "staticResources", key = "'fcfsSuccess'")
    public FcfsSuccessResult getFcfsSuccessResult(FcfsSettingDto firstFcfsSetting) {

        Map<String, String> textContentMap = staticResourceUtil.getTextContentMap();
        Map<String, String> s3ContentMap = staticResourceUtil.getS3ContentMap();

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

    /**
     * 선착순 실패 모달 정보 중, 정적 정보를 반환하는 메서드
     */
    @Cacheable(value = "staticResources", key = "'fcfsFail_' + #isDuplicated")
    public FcfsFailResult getFcfsFailResult(boolean isDuplicated) {
        Map<String, String> textContentMap = staticResourceUtil.getTextContentMap();

        if(isDuplicated){
            return FcfsFailResult.builder()
                    .title(textContentMap.get(StaticTextName.FCFS_DUPLICATED_TITLE.name()))
                    .subTitle(textContentMap.get(StaticTextName.FCFS_DUPLICATED_SUBTITLE.name()))
                    .caution(textContentMap.get(StaticTextName.FCFS_LOSER_CAUTION.name()))
                    .build();
        }
        return FcfsFailResult.builder()
                .title(textContentMap.get(StaticTextName.FCFS_LOSER_TITLE.name()))
                .subTitle(textContentMap.get(StaticTextName.FCFS_LOSER_SUBTITLE.name()))
                .caution(textContentMap.get(StaticTextName.FCFS_LOSER_CAUTION.name()))
                .build();
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

}
