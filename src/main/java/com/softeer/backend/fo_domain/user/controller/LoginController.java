package com.softeer.backend.fo_domain.user.controller;

import com.softeer.backend.fo_domain.user.dto.LoginRequest;
import com.softeer.backend.fo_domain.user.dto.UserTokenResponse;
import com.softeer.backend.fo_domain.user.service.LoginService;
import com.softeer.backend.global.common.code.status.SuccessStatus;
import com.softeer.backend.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    ResponseDto<UserTokenResponse> handleLogin(@Valid @RequestBody LoginRequest loginRequest){
        UserTokenResponse userTokenResponse = loginService.handleLogin(loginRequest);

        return ResponseDto.onSuccess(SuccessStatus._LOGIN_SUCCESS, userTokenResponse);
    }

}
