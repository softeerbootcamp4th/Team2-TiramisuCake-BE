package com.softeer.backend.fo_domain.user.service;

import com.softeer.backend.fo_domain.user.constatnt.RedisVerificationPrefix;
import com.softeer.backend.fo_domain.user.constatnt.VerificationProperty;
import com.softeer.backend.fo_domain.user.dto.verification.VerificationCodeResponseDto;
import com.softeer.backend.fo_domain.user.dto.verification.VerificationCodeTestResponseDto;
import com.softeer.backend.fo_domain.user.exception.UserException;
import com.softeer.backend.fo_domain.user.properties.SmsProperties;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.util.RandomCodeUtil;
import com.softeer.backend.global.util.StringRedisUtil;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class VerificationService {
    private final DefaultMessageService messageService;
    private final StringRedisUtil stringRedisUtil;
    private final SmsProperties smsProperties;
    private final RandomCodeUtil randomCodeUtil;

    public VerificationService(SmsProperties smsProperties, StringRedisUtil stringRedisUtil,
                               RandomCodeUtil randomCodeUtil) {
        this.messageService = NurigoApp.INSTANCE.initialize(
                smsProperties.getKey(), smsProperties.getSecret(), smsProperties.getUrl());
        this.smsProperties = smsProperties;
        this.stringRedisUtil = stringRedisUtil;
        this.randomCodeUtil = randomCodeUtil;
    }

    /**
     * 1. CoolSms를 사용하여 인증코드를 발급하고 인증 제한시간을 응답에 담아 반환한다.
     * 2. 인증 코드 발급 제한 횟수를 초과하면 내일 다시 인증하라는 응답을 전송한다.
     */
    public VerificationCodeResponseDto sendVerificationCode(String phoneNumber) {

        // 인증코드 발급이 처음이면 redis에 발급 횟수를 저장(유효 기간: 밤 12시 전까지)
        if (!stringRedisUtil.hasKey(RedisVerificationPrefix.VERIFICATION_ISSUE_COUNT.getPrefix() + phoneNumber)) {
            stringRedisUtil.setDataExpireAt(RedisVerificationPrefix.VERIFICATION_ISSUE_COUNT.getPrefix() + phoneNumber,
                    String.valueOf(1), LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1));

        }
        // 인증코드 발급 제한 횟수를 초과하면 예외 발생
        else {
            long issueCount = stringRedisUtil.incrementData(RedisVerificationPrefix.VERIFICATION_ISSUE_COUNT.getPrefix() + phoneNumber);
            if (issueCount > VerificationProperty.CODE_ISSUE_ATTEMPTS.getValue()) {
                log.error("Exceeded the number of code issuance attempts.");
                throw new UserException(ErrorStatus._AUTH_CODE_ISSUE_LIMIT_EXCEEDED);
            }
        }

        // 인증코드의 인증 횟수 삭제 (초기화 기능)
        stringRedisUtil.deleteData(RedisVerificationPrefix.VERIFICATION_ATTEMPTS.getPrefix() + phoneNumber);

        Message message = new Message();
        message.setFrom(smsProperties.getSenderNumber());
        message.setTo(phoneNumber);

        String verificationCode = randomCodeUtil.generateRandomCode(
                VerificationProperty.CODE_LENGTH.getValue());
        message.setText("[Hyundai] 본인 확인 인증번호는 (" + verificationCode + ") 입니다.");

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        log.info("Verification code sent to {} {}", phoneNumber, response);

        // 인증코드 저장(유효시간 설정)
        stringRedisUtil.setDataExpire(RedisVerificationPrefix.VERIFICATION_CODE.getPrefix() + phoneNumber, verificationCode,
                VerificationProperty.TIME_LIMIT.getValue());

        return VerificationCodeResponseDto.builder()
                .timeLimit(VerificationProperty.TIME_LIMIT.getValue())
                .build();
    }

    public VerificationCodeTestResponseDto sendVerificationCodeTest(String phoneNumber) {

        // 인증코드 발급이 처음이면 redis에 발급 횟수를 저장(유효 기간: 밤 12시 전까지)
        if (!stringRedisUtil.hasKey(RedisVerificationPrefix.VERIFICATION_ISSUE_COUNT.getPrefix() + phoneNumber)) {
            stringRedisUtil.setDataExpireAt(RedisVerificationPrefix.VERIFICATION_ISSUE_COUNT.getPrefix() + phoneNumber,
                    String.valueOf(1), LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1));

        }
        // 인증코드 발급 제한 횟수를 초과하면 예외 발생
        else {
            long issueCount = stringRedisUtil.incrementData(RedisVerificationPrefix.VERIFICATION_ISSUE_COUNT.getPrefix() + phoneNumber);
            if (issueCount > VerificationProperty.CODE_ISSUE_ATTEMPTS.getValue()) {
                log.error("Exceeded the number of code issuance attempts.");
                throw new UserException(ErrorStatus._AUTH_CODE_ISSUE_LIMIT_EXCEEDED);
            }
        }

        // 인증코드의 인증 횟수 삭제 (초기화 기능)
        stringRedisUtil.deleteData(RedisVerificationPrefix.VERIFICATION_ATTEMPTS.getPrefix() + phoneNumber);

        String verificationCode = randomCodeUtil.generateRandomCode(
                VerificationProperty.CODE_LENGTH.getValue());

        // 인증코드 저장(유효시간 설정)
        stringRedisUtil.setDataExpire(RedisVerificationPrefix.VERIFICATION_CODE.getPrefix() + phoneNumber, verificationCode,
                VerificationProperty.TIME_LIMIT.getValue());

        return VerificationCodeTestResponseDto.builder()
                .verificationCode(verificationCode)
                .timeLimit(VerificationProperty.TIME_LIMIT.getValue())
                .build();
    }


    /**
     * 1. 인증 코드를 검증하여 Redis에 있는 인증코도와 같은지를 검사한다.
     * 2. 제한시간이 지났거나 인증코드 불일치, 혹은 인증 제한 횟수를 초과한 경우 예외를 던진다.
     */
    public void confirmVerificationCode(String phoneNumber, String verificationCode) {

        // 인증코드의 인증 제한 횟수를 초과하면 예외 발생
        if (stringRedisUtil.hasKey(RedisVerificationPrefix.VERIFICATION_ATTEMPTS.getPrefix() + phoneNumber)) {
            long attemptCount = stringRedisUtil.incrementData(RedisVerificationPrefix.VERIFICATION_ATTEMPTS.getPrefix() + phoneNumber);
            if (attemptCount > VerificationProperty.MAX_ATTEMPTS.getValue()) {
                log.error("Verification code attempts exceeded.");
                throw new UserException(ErrorStatus._AUTH_CODE_ATTEMPTS_EXCEEDED);
            }
        }
        // 인증코드의 인증 횟수 설정(유효 기간: 밤 12시 전까지)
        else {
            stringRedisUtil.setDataExpireAt(RedisVerificationPrefix.VERIFICATION_ATTEMPTS.getPrefix() + phoneNumber,
                    String.valueOf(1), LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1));
        }

        String originalVerificationCode = stringRedisUtil.getData(RedisVerificationPrefix.VERIFICATION_CODE.getPrefix() + phoneNumber);

        if (originalVerificationCode == null) {
            log.error("Verification code has expired.");
            throw new UserException(ErrorStatus._AUTH_CODE_NOT_EXIST);
        }

        if (!originalVerificationCode.equals(verificationCode)) {
            log.error("Verification code does not match.");
            throw new UserException(ErrorStatus._AUTH_CODE_NOT_MATCH);
        }

        // 인증 성공
        // 인증 관련한 모든 데이터를 삭제
        stringRedisUtil.deleteData(RedisVerificationPrefix.VERIFICATION_ISSUE_COUNT.getPrefix() + phoneNumber);
        stringRedisUtil.deleteData(RedisVerificationPrefix.VERIFICATION_ATTEMPTS.getPrefix() + phoneNumber);
        stringRedisUtil.deleteData(RedisVerificationPrefix.VERIFICATION_CODE.getPrefix() + phoneNumber);


    }
}
