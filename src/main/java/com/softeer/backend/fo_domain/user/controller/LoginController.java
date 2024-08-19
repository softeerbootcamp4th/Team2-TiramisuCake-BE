package com.softeer.backend.fo_domain.user.controller;

import com.softeer.backend.fo_domain.user.dto.LoginRequestDto;
import com.softeer.backend.global.common.dto.JwtTokenResponseDto;
import com.softeer.backend.fo_domain.user.service.LoginService;
import com.softeer.backend.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    ResponseDto<JwtTokenResponseDto> handleLogin(@Valid @RequestBody LoginRequestDto loginRequestDto,
                                                 @Parameter(hidden = true) HttpSession session,
                                                 @RequestHeader(value = "shareCode", required = false) String shareCode) {
        JwtTokenResponseDto jwtTokenResponseDto = loginService.handleLogin(loginRequestDto, session, shareCode);

        return ResponseDto.onSuccess(jwtTokenResponseDto);
    }

}
