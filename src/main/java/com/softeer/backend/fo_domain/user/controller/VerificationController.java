package com.softeer.backend.fo_domain.user.controller;

import com.softeer.backend.fo_domain.user.dto.verification.ConfirmCodeRequest;
import com.softeer.backend.fo_domain.user.dto.verification.VerificationCodeRequest;
import com.softeer.backend.fo_domain.user.dto.verification.VerificationCodeResponse;
import com.softeer.backend.fo_domain.user.service.VerificationService;
import com.softeer.backend.global.common.code.status.SuccessStatus;
import com.softeer.backend.global.common.response.ResponseDto;
import com.sun.net.httpserver.Authenticator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseDto<VerificationCodeResponse> sendVerificationCode(@Valid @RequestBody VerificationCodeRequest verificationCodeRequest){

        VerificationCodeResponse response = verificationService.sendVerificationCode(verificationCodeRequest.getPhoneNumber());

        return ResponseDto.onSuccess(SuccessStatus._VERIFICATION_SEND, response);

    }

    @PostMapping("/confirm")
    public ResponseDto<Void> confirmVerificationCode(@Valid @RequestBody ConfirmCodeRequest confirmCodeRequest){

        verificationService.confirmVerificationCode(confirmCodeRequest.getPhoneNumber(), confirmCodeRequest.getVerificationCode());

        return ResponseDto.onSuccess(SuccessStatus._VERIFICATION_CONFIRM);
    }
}
