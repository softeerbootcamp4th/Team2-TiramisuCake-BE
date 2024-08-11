package com.softeer.backend.bo_domain.admin.service;

import com.softeer.backend.bo_domain.admin.domain.Admin;
import com.softeer.backend.bo_domain.admin.dto.login.AdminLoginRequestDto;
import com.softeer.backend.bo_domain.admin.exception.AdminException;
import com.softeer.backend.bo_domain.admin.repository.AdminRepository;
import com.softeer.backend.bo_domain.admin.util.PasswordEncoder;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminLoginService {

    private final AdminRepository adminRepository;
    private final JwtUtil jwtUtil;
    private final StringRedisUtil stringRedisUtil;
    private final PasswordEncoder passwordEncoder;

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

    public void handleLogout(int adminId) {

        stringRedisUtil.deleteRefreshToken(JwtClaimsDto.builder()
                .id(adminId)
                .roleType(RoleType.ROLE_ADMIN)
                .build());
    }
}
