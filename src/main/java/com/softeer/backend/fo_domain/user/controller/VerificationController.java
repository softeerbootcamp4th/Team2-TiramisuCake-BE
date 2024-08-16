package com.softeer.backend.fo_domain.user.controller;

import com.softeer.backend.fo_domain.user.dto.verification.ConfirmCodeRequestDto;
import com.softeer.backend.fo_domain.user.dto.verification.VerificationCodeRequestDto;
import com.softeer.backend.fo_domain.user.dto.verification.VerificationCodeResponseDto;
import com.softeer.backend.fo_domain.user.service.VerificationService;
import com.softeer.backend.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/verification")
public class VerificationController {
    private final VerificationService verificationService;

    @PostMapping("/send")
    public ResponseDto<VerificationCodeResponseDto> sendVerificationCode(@Valid @RequestBody VerificationCodeRequestDto verificationCodeRequestDto) {

        VerificationCodeResponseDto response = verificationService.sendVerificationCode(verificationCodeRequestDto.getPhoneNumber());

        return ResponseDto.onSuccess(response);

    }

    @PostMapping("/confirm")
    public ResponseDto<Void> confirmVerificationCode(@Valid @RequestBody ConfirmCodeRequestDto confirmCodeRequestDto) {

        verificationService.confirmVerificationCode(confirmCodeRequestDto.getPhoneNumber(), confirmCodeRequestDto.getVerificationCode());

        return ResponseDto.onSuccess();
    }
}
