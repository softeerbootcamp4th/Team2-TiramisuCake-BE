package com.softeer.backend.bo_domain.admin.controller;

import com.softeer.backend.bo_domain.admin.dto.login.AdminLoginRequestDto;
import com.softeer.backend.bo_domain.admin.service.AdminLoginService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.dto.JwtTokenResponseDto;
import com.softeer.backend.global.common.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminLoginController {

    private final AdminLoginService adminLoginService;

    @PostMapping("/login")
    ResponseDto<JwtTokenResponseDto> handleLogin(@Valid @RequestBody AdminLoginRequestDto adminLoginRequestDto) {
        JwtTokenResponseDto jwtTokenResponseDto = adminLoginService.handleLogin(adminLoginRequestDto);

        return ResponseDto.onSuccess(jwtTokenResponseDto);
    }

    @PostMapping("/logout")
    ResponseDto<Void> handleLogout(@AuthInfo Integer adminId) {
        adminLoginService.handleLogout(adminId);

        return ResponseDto.onSuccess();
    }


}
