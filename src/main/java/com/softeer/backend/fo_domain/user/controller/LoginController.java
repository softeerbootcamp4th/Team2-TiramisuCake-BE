package com.softeer.backend.fo_domain.user.controller;

import com.softeer.backend.fo_domain.user.dto.LoginRequestDto;
import com.softeer.backend.global.common.dto.JwtTokenResponseDto;
import com.softeer.backend.fo_domain.user.service.LoginService;
import com.softeer.backend.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인 요청을 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    /**
     * 로그인 요청을 처리하는 메서드
     */
    @PostMapping("/login")
    ResponseDto<JwtTokenResponseDto> handleLogin(@Valid @RequestBody LoginRequestDto loginRequestDto,
                                                 @RequestHeader(value = "X-Share-Code", required = false) String shareCode) {
        JwtTokenResponseDto jwtTokenResponseDto = loginService.handleLogin(loginRequestDto, shareCode);

        return ResponseDto.onSuccess(jwtTokenResponseDto);
    }
}
