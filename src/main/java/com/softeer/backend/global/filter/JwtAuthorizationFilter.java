package com.softeer.backend.global.filter;

import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RoleType;
import com.softeer.backend.global.common.entity.AuthInfo;
import com.softeer.backend.global.common.exception.JwtAuthorizationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 유저의 권한을 검증하는 필터 클래스
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!isAdminRequiredUri(request.getRequestURI())){
            filterChain.doFilter(request, response);
        }

        AuthInfo authInfo = (AuthInfo) request.getAttribute("authInfo");

        if(authInfo == null || authInfo.getRoleType()!= RoleType.ROLE_ADMIN)
            throw new JwtAuthorizationException(ErrorStatus._ACCESS_DENIED);
    }

    // Admin 권한이 필요한 uri인지를 체크하는 메서드
    private boolean isAdminRequiredUri(String uri) {
        return PatternMatchUtils.simpleMatch(uri, "/admin/*");
    }


}
