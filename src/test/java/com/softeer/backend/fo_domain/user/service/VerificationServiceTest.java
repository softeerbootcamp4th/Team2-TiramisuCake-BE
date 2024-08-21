package com.softeer.backend.fo_domain.user.service;

import com.softeer.backend.fo_domain.user.constatnt.RedisVerificationPrefix;
import com.softeer.backend.fo_domain.user.constatnt.VerificationProperty;
import com.softeer.backend.fo_domain.user.dto.verification.VerificationCodeResponseDto;
import com.softeer.backend.fo_domain.user.exception.UserException;
import com.softeer.backend.fo_domain.user.properties.SmsProperties;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.util.RandomCodeUtil;
import com.softeer.backend.global.util.StringRedisUtil;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {

    @Mock
    private StringRedisUtil stringRedisUtil;

    @Mock
    private RandomCodeUtil randomCodeUtil;

    @Mock
    private SmsProperties smsProperties;

    @Mock
    private DefaultMessageService messageService;

    @InjectMocks
    private VerificationService verificationService;

    private static final String PHONE_NUMBER = "01012345678";
    private static final String VERIFICATION_CODE = "123456";
    private static final long TIME_LIMIT = 300L;

    @Test
    @DisplayName("처음 인증 코드 발급 시 Redis에 발급 횟수 저장 및 코드 발송")
    void testSendVerificationCode_FirstTime() {
        // Given
        given(smsProperties.getSenderNumber()).willReturn("01000000000");
        given(randomCodeUtil.generateRandomCode(anyInt())).willReturn(VERIFICATION_CODE);
        given(stringRedisUtil.hasKey(anyString())).willReturn(false);

        // When
        VerificationCodeResponseDto response = verificationService.sendVerificationCode(PHONE_NUMBER);

        // Then
        verify(stringRedisUtil, times(1)).setDataExpireAt(anyString(), eq("1"), any(LocalDateTime.class));
        verify(stringRedisUtil, times(1)).deleteData(anyString());
        verify(messageService, times(1)).sendOne(any(SingleMessageSendingRequest.class));
        verify(stringRedisUtil, times(1)).setDataExpire(anyString(), eq(VERIFICATION_CODE), eq(TIME_LIMIT));

        assertThat(response).isNotNull();
        assertThat(response.getTimeLimit()).isEqualTo(TIME_LIMIT);
    }

    @Test
    @DisplayName("발급 횟수 초과 시 UserException 예외 발생")
    void testSendVerificationCode_ExceedIssueLimit() {
        // Given
        given(stringRedisUtil.hasKey(anyString())).willReturn(true);
        given(stringRedisUtil.incrementData(anyString())).willReturn((long) (VerificationProperty.CODE_ISSUE_ATTEMPTS.getValue() + 1));

        // When & Then
        assertThrows(UserException.class, () -> verificationService.sendVerificationCode(PHONE_NUMBER));

        verify(stringRedisUtil, never()).setDataExpireAt(anyString(), anyString(), any(LocalDateTime.class));
        verify(stringRedisUtil, never()).deleteData(anyString());
        verify(messageService, never()).sendOne(any(SingleMessageSendingRequest.class));
        verify(stringRedisUtil, never()).setDataExpire(anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("발급 횟수를 초과하지 않은 경우 정상적으로 인증 코드 발송")
    void testSendVerificationCode_NotExceedIssueLimit() {
        // Given
        given(smsProperties.getSenderNumber()).willReturn("01000000000");
        given(randomCodeUtil.generateRandomCode(anyInt())).willReturn(VERIFICATION_CODE);
        given(stringRedisUtil.hasKey(anyString())).willReturn(true);
        given(stringRedisUtil.incrementData(anyString())).willReturn(2L);

        // When
        VerificationCodeResponseDto response = verificationService.sendVerificationCode(PHONE_NUMBER);

        // Then
        verify(stringRedisUtil, never()).setDataExpireAt(anyString(), anyString(), any(LocalDateTime.class));
        verify(stringRedisUtil, times(1)).deleteData(anyString());
        verify(messageService, times(1)).sendOne(any(SingleMessageSendingRequest.class));
        verify(stringRedisUtil, times(1)).setDataExpire(anyString(), eq(VERIFICATION_CODE), eq(TIME_LIMIT));

        assertThat(response).isNotNull();
        assertThat(response.getTimeLimit()).isEqualTo(TIME_LIMIT);
    }

    @Test
    @DisplayName("인증 코드가 일치하고 인증에 성공하면 Redis에서 관련 데이터 삭제")
    void testConfirmVerificationCode_Success() {
        // given
        when(stringRedisUtil.getData(RedisVerificationPrefix.VERIFICATION_CODE.getPrefix() + PHONE_NUMBER))
                .thenReturn(VERIFICATION_CODE);

        // when
        verificationService.confirmVerificationCode(PHONE_NUMBER, VERIFICATION_CODE);

        // then
        verify(stringRedisUtil).deleteData(RedisVerificationPrefix.VERIFICATION_ISSUE_COUNT.getPrefix() + PHONE_NUMBER);
        verify(stringRedisUtil).deleteData(RedisVerificationPrefix.VERIFICATION_ATTEMPTS.getPrefix() + PHONE_NUMBER);
        verify(stringRedisUtil).deleteData(RedisVerificationPrefix.VERIFICATION_CODE.getPrefix() + PHONE_NUMBER);
    }

    @Test
    @DisplayName("인증 코드가 만료되었을 때 UserException 발생")
    void testConfirmVerificationCode_CodeExpired() {
        // given
        when(stringRedisUtil.getData(RedisVerificationPrefix.VERIFICATION_CODE.getPrefix() + PHONE_NUMBER))
                .thenReturn(null);

        // when / then
        UserException exception = catchThrowableOfType(
                () -> verificationService.confirmVerificationCode(PHONE_NUMBER, VERIFICATION_CODE),
                UserException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getCode()).isEqualTo(ErrorStatus._AUTH_CODE_NOT_EXIST);
    }

    @Test
    @DisplayName("인증 코드가 일치하지 않을 때 UserException 발생")
    void testConfirmVerificationCode_CodeNotMatch() {
        // given
        when(stringRedisUtil.getData(RedisVerificationPrefix.VERIFICATION_CODE.getPrefix() + PHONE_NUMBER))
                .thenReturn("654321");

        // when / then
        UserException exception = catchThrowableOfType(
                () -> verificationService.confirmVerificationCode(PHONE_NUMBER, VERIFICATION_CODE),
                UserException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getCode()).isEqualTo(ErrorStatus._AUTH_CODE_NOT_MATCH);
    }

    @Test
    @DisplayName("인증 시도 횟수를 초과하면 UserException 발생")
    void testConfirmVerificationCode_ExceedAttemptsLimit() {
        // given
        when(stringRedisUtil.hasKey(RedisVerificationPrefix.VERIFICATION_ATTEMPTS.getPrefix() + PHONE_NUMBER))
                .thenReturn(true);
        when(stringRedisUtil.incrementData(RedisVerificationPrefix.VERIFICATION_ATTEMPTS.getPrefix() + PHONE_NUMBER))
                .thenReturn((long) (VerificationProperty.MAX_ATTEMPTS.getValue() + 1));

        // when / then
        UserException exception = catchThrowableOfType(
                () -> verificationService.confirmVerificationCode(PHONE_NUMBER, VERIFICATION_CODE),
                UserException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getCode()).isEqualTo(ErrorStatus._AUTH_CODE_ATTEMPTS_EXCEEDED);
    }
}
