package com.softeer.backend.fo_domain.fcfs.service;

import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsPageResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsRequestDto;
import com.softeer.backend.fo_domain.fcfs.dto.FcfsSettingDto;
import com.softeer.backend.fo_domain.fcfs.dto.QuizDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsFailResult;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsResultResponseDto;
import com.softeer.backend.fo_domain.fcfs.dto.result.FcfsSuccessResult;
import com.softeer.backend.fo_domain.fcfs.exception.FcfsException;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.staticresources.constant.StaticTextName;
import com.softeer.backend.global.staticresources.util.StaticResourceUtil;
import com.softeer.backend.global.util.FcfsRedisUtil;
import com.softeer.backend.global.util.RandomCodeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FcfsServiceTest {

    @Mock
    private StaticResourceUtil staticResourceUtil;

    @Mock
    private QuizManager quizManager;

    @Mock
    private FcfsSettingManager fcfsSettingManager;

    @Mock
    private DrawSettingManager drawSettingManager;

    @Mock
    private FcfsRedisUtil fcfsRedisUtil;

    @Mock
    private RandomCodeUtil randomCodeUtil;

    @Mock
    private ObjectProvider<FcfsService> fcfsServiceProvider;

    private FcfsService mockFcfsService;

    @InjectMocks
    private FcfsService fcfsService;

    private FcfsRequestDto fcfsRequestDto;
    private String correctAnswer = "correct";
    private String wrongAnswer = "wrong";
    private int userId = 1;
    private int round = 1;

    @BeforeEach
    void setUp() {
        fcfsRequestDto = new FcfsRequestDto();

        mockFcfsService = mock(FcfsService.class);


    }

    @Test
    @DisplayName("선착순 페이지에 필요한 정보를 반환한다.")
    void testGetFcfsPage() {
        // Given
        QuizDto quizDto = QuizDto.builder()
                .answerWord("correct")
                .answerSentence("Answer sentence")
                .startIndex(1)
                .endIndex(10)
                .build();

        Map<String, String> textContentMap = new HashMap<>();
        textContentMap.put(StaticTextName.FCFS_QUIZ_DESCRIPTION.name(), "Description: %d");

        when(staticResourceUtil.format(anyString(), anyInt())).thenAnswer(invocation -> {
            String template = invocation.getArgument(0);
            Integer winnerNum = invocation.getArgument(1);
            return String.format(template, winnerNum);
        });

        when(quizManager.getQuiz(round)).thenReturn(quizDto);
        when(staticResourceUtil.getTextContentMap()).thenReturn(textContentMap);
        when(fcfsSettingManager.getFcfsWinnerNum()).thenReturn(100);

        // When
        FcfsPageResponseDto response = fcfsService.getFcfsPage(round);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAnswerWord()).isEqualTo("correct");
        assertThat(response.getAnswerSentence()).isEqualTo("Answer sentence");
        assertThat(response.getStartIndex()).isEqualTo(1);
        assertThat(response.getEndIndex()).isEqualTo(10);
        assertThat(response.getQuizDescription()).isEqualTo("Description: 100");
    }

    @Test
    @DisplayName("선착순 이벤트 처리 성공 시 성공 응답을 반환한다.")
    void testHandleFcfsEvent_Success() {
        // Given
        fcfsRequestDto.setAnswer(correctAnswer);
        when(fcfsServiceProvider.getObject()).thenReturn(mockFcfsService);
        when(quizManager.getQuiz(round)).thenReturn(new QuizDto("hint", correctAnswer, "Answer sentence", 1, 10));
        when(fcfsSettingManager.isFcfsClosed()).thenReturn(false);

        FcfsResultResponseDto successResponse = FcfsResultResponseDto.builder()
                .isFcfsWinner(true)
                .build();
        when(mockFcfsService.saveFcfsWinners(userId, round)).thenReturn(successResponse);

        // When
        FcfsResultResponseDto response = fcfsService.handleFcfsEvent(userId, round, fcfsRequestDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isFcfsWinner()).isTrue();
    }

    @Test
    @DisplayName("선착순 이벤트 처리 시 퀴즈 정답이 틀리면 예외가 발생한다.")
    void testHandleFcfsEvent_WrongAnswer() {
        // Given
        fcfsRequestDto.setAnswer(wrongAnswer);
        when(quizManager.getQuiz(round)).thenReturn(new QuizDto("hint", correctAnswer, "Answer sentence", 1, 10));

        // When / Then
        assertThatThrownBy(() -> fcfsService.handleFcfsEvent(userId, round, fcfsRequestDto))
                .isInstanceOf(FcfsException.class)
                .extracting(ex -> ((FcfsException) ex).getCode())
                .isEqualTo(ErrorStatus._BAD_REQUEST);
    }

    @Test
    @DisplayName("선착순 이벤트가 마감되었을 때 선착순 실패 응답을 반환한다.")
    void testHandleFcfsEvent_Closed() {
        // Given
        fcfsRequestDto.setAnswer(correctAnswer);
        when(quizManager.getQuiz(round)).thenReturn(new QuizDto("hint", correctAnswer, "Answer sentence", 1, 10));
        when(fcfsSettingManager.isFcfsClosed()).thenReturn(true);
        when(fcfsServiceProvider.getObject()).thenReturn(mockFcfsService);

        // When
        FcfsResultResponseDto response = fcfsService.handleFcfsEvent(userId, round, fcfsRequestDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isFcfsWinner()).isFalse();
    }

    @Test
    @DisplayName("선착순 당첨자 등록 성공 시 성공 응답을 반환한다.")
    void testSaveFcfsWinners_Success() {
        // Given
        when(fcfsRedisUtil.getIntegerSetSize(anyString())).thenReturn(0L);
        when(fcfsSettingManager.getFcfsWinnerNum()).thenReturn(100);
        when(fcfsRedisUtil.isValueInIntegerSet(anyString(), anyInt())).thenReturn(false);
        when(randomCodeUtil.generateRandomCode(5)).thenReturn("ABCDE");
        when(fcfsServiceProvider.getObject()).thenReturn(mockFcfsService);

        when(staticResourceUtil.getTextContentMap()).thenReturn(new HashMap<>());
        when(staticResourceUtil.getTextContentMap()).thenReturn(new HashMap<>());
        FcfsSettingDto mockSettingDto = new FcfsSettingDto();
        when(fcfsSettingManager.getFcfsSettingByRound(1)).thenReturn(mockSettingDto);

        FcfsSuccessResult successResult = FcfsSuccessResult.builder()
                .fcfsCode("ABCDE")
                .build();

        when(mockFcfsService.getFcfsSuccessResult(anyMap(), anyMap(), any(FcfsSettingDto.class)))
                .thenReturn(successResult);

        // When
        FcfsResultResponseDto response = fcfsService.saveFcfsWinners(userId, round);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isFcfsWinner()).isTrue();
        assertThat(response.getFcfsResult()).isNotNull();
        assertThat(((FcfsSuccessResult) (response.getFcfsResult())).getFcfsCode()).isEqualTo("AABCDE");
    }

    @Test
    @DisplayName("선착순 당첨자 등록 실패 시 실패 응답을 반환한다.")
    void testSaveFcfsWinners_Failure() {
        // Given
        when(fcfsRedisUtil.getIntegerSetSize(anyString())).thenReturn(100L);  // 이미 모든 당첨자가 존재함
        when(fcfsSettingManager.getFcfsWinnerNum()).thenReturn(100);
        when(fcfsServiceProvider.getObject()).thenReturn(mockFcfsService);

        // When
        FcfsResultResponseDto response = fcfsService.saveFcfsWinners(userId, round);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isFcfsWinner()).isFalse();
    }

    @Test
    @DisplayName("선착순 성공 결과 모달 정보를 반환한다.")
    void testGetFcfsResult_Success() {
        // Given
        Map<String, String> textContentMap = new HashMap<>();
        Map<String, String> s3ContentMap = new HashMap<>();
        FcfsSettingDto fcfsSettingDto = FcfsSettingDto.builder()
                .round(1)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .winnerNum(10)
                .build();
        when(fcfsServiceProvider.getObject()).thenReturn(mockFcfsService);
        when(staticResourceUtil.getTextContentMap()).thenReturn(textContentMap);
        when(staticResourceUtil.getS3ContentMap()).thenReturn(s3ContentMap);
        when(fcfsSettingManager.getFcfsSettingByRound(1)).thenReturn(fcfsSettingDto);

        FcfsSuccessResult successResult = FcfsSuccessResult.builder()
                .fcfsCode("ABCDE")
                .build();

        when(mockFcfsService.getFcfsSuccessResult(anyMap(), anyMap(), any(FcfsSettingDto.class)))
                .thenReturn(successResult);


        // When
        FcfsResultResponseDto response = fcfsService.getFcfsResult(true, "A12345");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isFcfsWinner()).isTrue();
    }
}
