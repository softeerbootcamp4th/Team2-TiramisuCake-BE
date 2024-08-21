package com.softeer.backend.fo_domain.admin.service;

import com.softeer.backend.bo_domain.admin.domain.Admin;
import com.softeer.backend.bo_domain.admin.dto.login.AdminLoginRequestDto;
import com.softeer.backend.bo_domain.admin.dto.login.AdminSignUpRequestDto;
import com.softeer.backend.bo_domain.admin.exception.AdminException;
import com.softeer.backend.bo_domain.admin.repository.AdminRepository;
import com.softeer.backend.bo_domain.admin.service.AdminLoginService;
import com.softeer.backend.bo_domain.admin.util.PasswordEncoder;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RoleType;
import com.softeer.backend.global.common.dto.JwtClaimsDto;
import com.softeer.backend.global.common.dto.JwtTokenResponseDto;
import com.softeer.backend.global.util.JwtUtil;
import com.softeer.backend.global.util.StringRedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminLoginServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private StringRedisUtil stringRedisUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminLoginService adminLoginService;

    private Admin admin;
    private AdminLoginRequestDto loginRequestDto;
    private AdminSignUpRequestDto signUpRequestDto;
    private JwtTokenResponseDto jwtTokenResponseDto;

    @BeforeEach
    void setUp() {
        admin = Admin.builder()
                .id(1)
                .account("admin")
                .password("encodedPassword")
                .build();

        loginRequestDto = AdminLoginRequestDto.builder()
                .account("admin")
                .password("plainPassword")
                .build();

        signUpRequestDto = AdminSignUpRequestDto.builder()
                .account("newAdmin")
                .password("newPassword")
                .build();

        jwtTokenResponseDto = JwtTokenResponseDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .expiredTime(LocalDateTime.now().plusHours(1))
                .build();
    }

    @Test
    @DisplayName("어드민 로그인 성공 시 JWT 토큰을 반환한다.")
    void handleLoginSuccess() {
        // Given
        when(adminRepository.findByAccount("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(jwtUtil.createServiceToken(any())).thenReturn(jwtTokenResponseDto);

        // When
        JwtTokenResponseDto response = adminLoginService.handleLogin(loginRequestDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(response.getExpiredTime()).isNotNull();
    }

    @Test
    @DisplayName("어드민 로그인 시 비밀번호 불일치 시 예외가 발생한다.")
    void handleLoginPasswordMismatch() {
        // Given
        when(adminRepository.findByAccount("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(false);

        // When & Then
        AdminException exception = assertThrows(AdminException.class, () -> adminLoginService.handleLogin(loginRequestDto));
        assertThat(exception.getCode()).isEqualTo(ErrorStatus._NOT_FOUND);
    }

    @Test
    @DisplayName("어드민 로그아웃 시 Redis에서 refresh token을 삭제한다.")
    void handleLogout() {
        // Given
        int adminId = 1;

        // When
        adminLoginService.handleLogout(adminId);

        // Then
        verify(stringRedisUtil).deleteRefreshToken(argThat(jwtClaims ->
                jwtClaims.getId() == adminId &&
                        jwtClaims.getRoleType() == RoleType.ROLE_ADMIN
        ));
    }

    @Test
    @DisplayName("어드민 회원가입 시 계정이 이미 존재하면 예외가 발생한다.")
    void handleSignUpAccountAlreadyExists() {
        // Given
        when(adminRepository.existsByAccount("newAdmin")).thenReturn(true);

        // When & Then
        AdminException exception = assertThrows(AdminException.class, () -> adminLoginService.handleSignUp(signUpRequestDto));
        assertThat(exception.getCode()).isEqualTo(ErrorStatus._BAD_REQUEST);
    }
}