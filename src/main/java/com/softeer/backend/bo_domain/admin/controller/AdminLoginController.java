package com.softeer.backend.bo_domain.admin.controller;

import com.softeer.backend.bo_domain.admin.dto.DrawSettingTestRequestDto;
import com.softeer.backend.bo_domain.admin.dto.FcfsSettingTestRequestDto;
import com.softeer.backend.bo_domain.admin.dto.login.AdminLoginRequestDto;
import com.softeer.backend.bo_domain.admin.dto.login.AdminSignUpRequestDto;
import com.softeer.backend.bo_domain.admin.service.AdminLoginService;
import com.softeer.backend.global.annotation.AuthInfo;
import com.softeer.backend.global.common.dto.JwtTokenResponseDto;
import com.softeer.backend.global.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 어드민 계정 로그인 및 로그아웃을 관리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminLoginController {
    private final AdminLoginService adminLoginService;

    /**
     * 어드민 계정 로그인을 처리하는 메서드
     */
    @PostMapping("/login")
    ResponseDto<JwtTokenResponseDto> handleLogin(@Valid @RequestBody AdminLoginRequestDto adminLoginRequestDto) {
        JwtTokenResponseDto jwtTokenResponseDto = adminLoginService.handleLogin(adminLoginRequestDto);

        return ResponseDto.onSuccess(jwtTokenResponseDto);
    }

    /**
     * 어드민 계정 로그아웃을 처리하는 메서드
     */
    @PostMapping("/logout")
    ResponseDto<Void> handleLogout(@Parameter(hidden = true) @AuthInfo Integer adminId) {
        adminLoginService.handleLogout(adminId);

        return ResponseDto.onSuccess();
    }

    /**
     * 어드민 계정 회원가입을 처리하는 메서드
     */
    @PostMapping("/signup")
    ResponseDto<Void> handleSignUp(@Valid @RequestBody AdminSignUpRequestDto adminSignUpRequestDto) {

        adminLoginService.handleSignUp(adminSignUpRequestDto);

        return ResponseDto.onSuccess();
    }

    /**
     * 선착순 설정 정보를 바로 반영하게 하는 테스트용 메서드
     */
    @PostMapping("/fcfs/test")
    ResponseDto<Void> setFcfsSetting(@RequestBody FcfsSettingTestRequestDto fcfsSettingTestRequestDto) {

        adminLoginService.setFcfsSetting(fcfsSettingTestRequestDto);

        return ResponseDto.onSuccess();
    }

    /**
     * 추첨 설정 정보를 바로 반영하게 하는 테스트용 메서드
     */
    @PostMapping("/draw/test")
    ResponseDto<Void> setDrawSetting(@RequestBody DrawSettingTestRequestDto drawSettingTestRequestDto) {

        adminLoginService.setDrawSetting(drawSettingTestRequestDto);

        return ResponseDto.onSuccess();
    }
}
