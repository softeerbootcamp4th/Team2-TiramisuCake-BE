package com.softeer.backend.bo_domain.admin.service;

import com.softeer.backend.bo_domain.admin.domain.Admin;
import com.softeer.backend.bo_domain.admin.dto.DrawSettingTestRequestDto;
import com.softeer.backend.bo_domain.admin.dto.FcfsSettingTestRequestDto;
import com.softeer.backend.bo_domain.admin.dto.login.AdminLoginRequestDto;
import com.softeer.backend.bo_domain.admin.dto.login.AdminSignUpRequestDto;
import com.softeer.backend.bo_domain.admin.exception.AdminException;
import com.softeer.backend.bo_domain.admin.repository.AdminRepository;
import com.softeer.backend.bo_domain.admin.util.PasswordEncoder;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RoleType;
import com.softeer.backend.global.common.dto.JwtClaimsDto;
import com.softeer.backend.global.common.dto.JwtTokenResponseDto;
import com.softeer.backend.global.util.JwtUtil;
import com.softeer.backend.global.util.StringRedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 어드민 계정 관련 기능을 처리하는 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminLoginService {

    private final AdminRepository adminRepository;
    private final JwtUtil jwtUtil;
    private final StringRedisUtil stringRedisUtil;
    private final PasswordEncoder passwordEncoder;
    private final FcfsSettingManager fcfsSettingManager;
    private final DrawSettingManager drawSettingManager;

    /**
     * 어드민 계정 로그인을 처리하는 메서드
     * <p>
     * 1. 요청 Dto에 있는 어드민 계정으로 DB에서 Admin 엔티티를 조회한다.
     * 2. 사용자가 입력한 비밀번호가 유효한지 확인한다.
     * 2-1. 유효하다면 Jwt 응답을 반환한다.
     * 2-2. 유효하지 않으면 예외 발생한다.
     */
    @Transactional(readOnly = true)
    public JwtTokenResponseDto handleLogin(AdminLoginRequestDto adminLoginRequestDto) {

        Admin admin = adminRepository.findByAccount(adminLoginRequestDto.getAccount())
                .orElseThrow(() -> {
                    log.error("Admin not found.");
                    return new AdminException(ErrorStatus._NOT_FOUND);
                });

        if (!passwordEncoder.matches(adminLoginRequestDto.getPassword(), admin.getPassword())) {
            log.error("Admin password not match.");
            throw new AdminException(ErrorStatus._NOT_FOUND);
        }

        return jwtUtil.createServiceToken(JwtClaimsDto.builder()
                .id(admin.getId())
                .roleType(RoleType.ROLE_ADMIN)
                .build());

    }

    /**
     * 어드민 계정 로그아웃을 처리하는 메서드
     * <p>
     * 1. Redis에 저장된 유저의 refresh token을 삭제한다.
     */
    public void handleLogout(int adminId) {

        stringRedisUtil.deleteRefreshToken(JwtClaimsDto.builder()
                .id(adminId)
                .roleType(RoleType.ROLE_ADMIN)
                .build());
    }

    /**
     * 어드민 계정 회원가입을 처리하는 메서드
     * <p>
     * 1. 이미 계정이 있다면 예외가 발생한다.
     * 2. 새로운 계정이라면 DB에 저장한다.
     */
    @Transactional
    public void handleSignUp(AdminSignUpRequestDto adminSignUpRequestDto) {

        if (adminRepository.existsByAccount(adminSignUpRequestDto.getAccount())) {
            log.error("Admin account already exist.");
            throw new AdminException(ErrorStatus._BAD_REQUEST);
        }

        adminRepository.save(Admin.builder()
                .account(adminSignUpRequestDto.getAccount())
                .password(passwordEncoder.encode(adminSignUpRequestDto.getPassword()))
                .build());
    }

    public void setFcfsSetting(FcfsSettingTestRequestDto fcfsSettingTestRequestDto) {
        fcfsSettingManager.setFcfsSettingByAdmin(fcfsSettingTestRequestDto);
    }

    public void setDrawSetting(DrawSettingTestRequestDto drawSettingTestRequestDto) {
        drawSettingManager.setDrawSettingByAdmin(drawSettingTestRequestDto);
    }
}
