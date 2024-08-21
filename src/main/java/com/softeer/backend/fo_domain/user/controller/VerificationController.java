package com.softeer.backend.fo_domain.user.controller;

import com.softeer.backend.fo_domain.user.dto.verification.ConfirmCodeRequestDto;
import com.softeer.backend.fo_domain.user.dto.verification.VerificationCodeRequestDto;
import com.softeer.backend.fo_domain.user.dto.verification.VerificationCodeResponseDto;
import com.softeer.backend.fo_domain.user.dto.verification.VerificationCodeTestResponseDto;
import com.softeer.backend.fo_domain.user.service.VerificationService;
import com.softeer.backend.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 전화번호 인증을 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/verification")
public class VerificationController {
    private final VerificationService verificationService;

    /**
     * 요청으로 들어온 전화번호로 인증코드를 발송하는 메서드
     */
    @PostMapping("/send")
    public ResponseDto<VerificationCodeResponseDto> sendVerificationCode(@Valid @RequestBody VerificationCodeRequestDto verificationCodeRequestDto) {

        VerificationCodeResponseDto response = verificationService.sendVerificationCode(verificationCodeRequestDto.getPhoneNumber());

        return ResponseDto.onSuccess(response);

    }

    /**
     * 인증코드를 응답 dto에 담아 반환해주는 test용 메서드
     */
    @PostMapping("/send/test")
    public ResponseDto<VerificationCodeTestResponseDto> sendVerificationCodeTest(@Valid @RequestBody VerificationCodeRequestDto verificationCodeRequestDto) {

        VerificationCodeTestResponseDto response = verificationService.sendVerificationCodeTest(verificationCodeRequestDto.getPhoneNumber());

        return ResponseDto.onSuccess(response);

    }

    /**
     * 인증번호가 유효한지 확인하는 메서드
     */
    @PostMapping("/confirm")
    public ResponseDto<Void> confirmVerificationCode(@Valid @RequestBody ConfirmCodeRequestDto confirmCodeRequestDto) {

        verificationService.confirmVerificationCode(confirmCodeRequestDto.getPhoneNumber(), confirmCodeRequestDto.getVerificationCode());

        return ResponseDto.onSuccess();
    }
}
