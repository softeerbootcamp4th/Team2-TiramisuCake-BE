package com.softeer.backend.global.filter;

import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.constant.RoleType;
import com.softeer.backend.global.common.dto.JwtClaimsDto;
import com.softeer.backend.global.common.exception.JwtAuthorizationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 유저의 권한을 검증하는 필터 클래스
 */
@Slf4j
@NoArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    // 인가검사를 하지 않는 url 설정
    private final String[] whiteListUrls = {
            "/admin/login", "/admin/signup"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(CorsUtils.isPreFlightRequest(request) || isUriInWhiteList(request.getRequestURI())){
            filterChain.doFilter(request, response);
            return;
        }


        JwtClaimsDto jwtClaimsDto = (JwtClaimsDto) request.getAttribute("jwtClaims");

        // 인증 정보가 없거나 RoleType이 ADMIN이 아닌경우, 인가 예외 발생
        if (jwtClaimsDto == null || jwtClaimsDto.getRoleType() != RoleType.ROLE_ADMIN) {

            log.error("JwtAuthorizationException has occurred");
            throw new JwtAuthorizationException(ErrorStatus._FORBIDDEN);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isUriInWhiteList(String url) {
        return PatternMatchUtils.simpleMatch(whiteListUrls, url);
    }

}
