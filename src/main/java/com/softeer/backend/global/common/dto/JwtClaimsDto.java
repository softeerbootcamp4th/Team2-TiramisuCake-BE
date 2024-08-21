package com.softeer.backend.global.common.dto;

import com.softeer.backend.global.common.constant.RoleType;
import lombok.Builder;
import lombok.Getter;

/**
 * JWT의 claim안에 있는 정보를 저장하는 Dto 클래스
 */
@Getter
@Builder
public class JwtClaimsDto {
    // User 또는 Admin 테이블의 primary key
    private int id;
    // 유저의 권한 정보
    private RoleType roleType;
}