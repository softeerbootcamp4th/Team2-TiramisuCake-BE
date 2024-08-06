package com.softeer.backend.global.filter;

import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RoleType;
import com.softeer.backend.global.common.entity.JwtClaimsDto;
import com.softeer.backend.global.common.exception.JwtAuthorizationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 유저의 권한을 검증하는 필터 클래스
 */
@NoArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        JwtClaimsDto jwtClaimsDto = (JwtClaimsDto) request.getAttribute("jwtClaims");

        if(jwtClaimsDto == null || jwtClaimsDto.getRoleType()!= RoleType.ROLE_ADMIN)
            throw new JwtAuthorizationException(ErrorStatus._ACCESS_DENIED);

        filterChain.doFilter(request, response);
    }

}
