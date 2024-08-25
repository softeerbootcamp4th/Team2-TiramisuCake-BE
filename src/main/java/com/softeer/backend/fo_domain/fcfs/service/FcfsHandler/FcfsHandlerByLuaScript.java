package com.softeer.backend.fo_domain.fcfs.service.FcfsHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsRequestDto;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsSettingDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsFailResult;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResultResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsSuccessResult;
import com.softeer.backend.fo_domain.fcfs.exception.FcfsException;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsRepository;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import com.softeer.backend.fo_domain.fcfs.service.QuizManager;
import com.softeer.backend.fo_domain.fcfs.service.test.FcfsCount;
import com.softeer.backend.fo_domain.fcfs.service.test.FcfsCountRepository;
import com.softeer.backend.fo_domain.fcfs.service.test.LuaRedisUtil;
import com.softeer.backend.fo_domain.user.domain.User;
import com.softeer.backend.fo_domain.user.repository.UserRepository;
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
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcfsHandlerByLuaScript implements FcfsHandler {

    private final LuaRedisUtil luaRedisUtil;
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M월 d일");
    private final ObjectProvider<FcfsHandlerByLuaScript> fcfsHandlerProvider;

    private final FcfsSettingManager fcfsSettingManager;
    private final DrawSettingManager drawSettingManager;
    private final QuizManager quizManager;
    private final FcfsRedisUtil fcfsRedisUtil;
    private final RandomCodeUtil randomCodeUtil;
    private final StaticResourceUtil staticResourceUtil;

 
    /**
     * 선착순 등록을 처리하고 결과 모달 정보를 반환하는 메서드
     * <p>
     * 1. 선착순 등록 요청 dto에서 전달된 퀴즈 정답이 유효한지 확인한다.
     * 1-1. 유효하지 않다면 예외가 발생한다.
     * 2. 선착순 설정 매니저 클래스의 fcfsClosed 변수값을 확인한다.(선착순 당첨자가 다 나왔는지 여부를 의미)
     * 2-1. 값이 true라면 선착순 이벤트 참여자 수에 1을 더하고 실패 모달 정보를 반환한다.
     * 2-2. 값이 false라면 선착순 등록을 처리하는 메서드를 호출한다.
     */
    @Override
    @Transactional(readOnly = true)
    public FcfsResultResponseDto handleFcfsEvent(int userId, int round, FcfsRequestDto fcfsRequestDto) throws JsonProcessingException {

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
        FcfsHandlerByLuaScript fcfsHandlerByLuaScript = fcfsHandlerProvider.getObject();
        return fcfsHandlerByLuaScript.saveFcfsWinners(userId, round);
    }


    public FcfsResultResponseDto saveFcfsWinners(int userId, int round) throws JsonProcessingException {

        int maxWinners = fcfsSettingManager.getFcfsWinnerNum();

        // Define the Redis keys
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
            case "FCFS_CLOSED":
            case "FCFS_CODE_EMPTY":

                return getFcfsResult(false, false, null);

            case "DUPLICATED":

                return getFcfsResult(false, true, null);

            default:
                String code = result;
                if (result.contains("_CLOSED")) {
                    fcfsSettingManager.setFcfsClosed(true);
                    code = result.replace("_CLOSED", "");
                }

                return getFcfsResult(true, false, code);
        }

    }


    /**
     * 선착순 이벤트 코드를 반환하는 메서드
     * <p>
     * round값에 따라 코드의 앞부분을 특정 문자로 고정한다.
     */
    private String makeFcfsCode(int round) {
        return (char) ('A' + round - 1) + randomCodeUtil.generateRandomCode(5);
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

        FcfsHandlerByLuaScript fcfsHandlerByLuaScript = fcfsHandlerProvider.getObject();

        if (fcfsWin) {
            FcfsSuccessResult fcfsSuccessResult = fcfsHandlerByLuaScript.getFcfsSuccessResult(firstFcfsSetting);
            fcfsSuccessResult.setFcfsCode(fcfsCode);

            return FcfsResultResponseDto.builder()
                    .fcfsWinner(fcfsWin)
                    .fcfsResult(fcfsSuccessResult)
                    .build();
        }

        FcfsFailResult fcfsFailResult = fcfsHandlerByLuaScript.getFcfsFailResult(isDuplicated);

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

        if (isDuplicated) {
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
}


