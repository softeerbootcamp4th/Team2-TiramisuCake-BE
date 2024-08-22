package com.softeer.backend.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.softeer.backend.global.common.code.status.ErrorStatus;
import com.softeer.backend.global.common.dto.JwtClaimsDto;
import com.softeer.backend.global.common.exception.JwtAuthenticationException;
import com.softeer.backend.global.common.response.ResponseDto;
import com.softeer.backend.global.config.properties.JwtProperties;
import com.softeer.backend.global.util.JwtUtil;
import com.softeer.backend.global.util.StringRedisUtil;
import com.softeer.backend.global.common.dto.JwtTokenResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

/**
 * Jwt 인증을 처리하는 필터 클래스
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 인증검사를 하지 않는 url 설정
    private final String[] whiteListUrls = {
            "/swagger-ui/**", "/swagger", "/v3/**", "/error/**",
            "/verification/send", "/verification/confirm", "/verification/send/test",
            "/login",
            "/main/event/static", "/main/event/info", "/main/car",
            "/admin/login", "/admin/signup",
            "/share/**",
            "/admin/fcfs/test","/admin/draw/test"
    };

    // Access Token이 header에 있으면 인증하고 없으면 인증하지 않는 url 설정
    private final String[] optionalAuthUrls = {
            "/comment",
            "/share-shorten-url"
    };

    private final JwtUtil jwtUtil;
    private final StringRedisUtil stringRedisUtil;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("request uri: {}", request.getRequestURI());

        // preflight 요청 또는 whitelist에 있는 요청은 인증 검사 x
        if (CorsUtils.isPreFlightRequest(request) || isUriInWhiteList(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // optionalAuthUrls에 등록된 url 중, access token이 header에 없으면 인증 x
        if (isUriInOptionalAuthList(request.getRequestURI()) &&
                jwtUtil.extractAccessToken(request).isEmpty()) {

            filterChain.doFilter(request, response);
            return;
        }


        // Case 01) Access Token 재발급인 경우(Authorization Header Access Token 유효성 x)
        if (request.getRequestURI().contains("/reissue")) {
            String accessToken = jwtUtil.extractAccessToken(request).orElseThrow(() -> {
                log.error("Access Token is missing in the Authorization header during the '/reissue' process.");
                return new JwtAuthenticationException(ErrorStatus._REISSUE_ERROR);
            });
            String refreshToken = jwtUtil.extractRefreshToken(request).orElseThrow(() -> {
                log.error("Refresh Token is missing in the Authorization header during the '/reissue' process.");
                return new JwtAuthenticationException(ErrorStatus._REISSUE_ERROR);
            });

            this.reissueAccessTokenAndRefreshToken(response, accessToken, refreshToken);
        }
        // Case 02) 일반 API 요청인 경우
        else {
            checkAccessToken(request);
            log.info("jwtAuthentication filter is finished");

            // Authentication Exception 없이 정상 인증처리 된 경우
            // 기존 필터 체인 호출
            filterChain.doFilter(request, response);
        }
    }

    /**
     * url이 white list에 속하는지를 검사하는 메서드
     */
    private boolean isUriInWhiteList(String url) {
        return PatternMatchUtils.simpleMatch(whiteListUrls, url);
    }

    /**
     * url이 optionalAuthUrls에 속하는지를 검사하는 메서드
     */
    private boolean isUriInOptionalAuthList(String url) {
        return PatternMatchUtils.simpleMatch(optionalAuthUrls, url);
    }

    /**
     * Access & Refresh Token을 재발급하는 메서드
     *
     * 1. refresh token 유효성 검증
     * 2. access token 유효성 검증(유효하지 않아야 함)
     * 3. redis refresh 와 일치 여부 확인
     */
    private void reissueAccessTokenAndRefreshToken(HttpServletResponse response,
                                                   String accessToken, String refreshToken) throws IOException {

        checkAllConditions(accessToken, refreshToken);
        String newAccessToken = jwtUtil.createAccessToken(jwtUtil.getJwtClaimsFromRefreshToken(refreshToken));
        String newRefreshToken = reIssueRefreshToken(jwtUtil.getJwtClaimsFromRefreshToken(refreshToken));
        makeAndSendAccessTokenAndRefreshToken(response, newAccessToken, newRefreshToken);
    }

    /**
     * Access & Refresh Token을 재발급 해야하는 조건인지를 확인하는 메서드
     *
     * 1. access Token 유효하지 않은지 확인
     * 2. refresh Token 유효한지 확인
     * 3. refresh Token 일치하는지 확인
     **/
    private void checkAllConditions(String accessToken, String refreshToken) {

        validateAccessToken(accessToken);
        validateRefreshToken(refreshToken);
        isRefreshTokenMatch(refreshToken);
    }

    /**
     * accessToken이 유효하지 않는지를 확인하는 메서드
     *
     * accessToken이 유효하면 재발급을 하면 안되므로 예외 발생
     */
    private void validateAccessToken(String accessToken) {
        if (jwtUtil.validateToken(accessToken)) {
            log.error("JWT Access Token is valid during the '/reissue' process.");
            throw new JwtAuthenticationException(ErrorStatus._REISSUE_ERROR);
        }
    }

    /**
     * refreshToken이 유효한지를 확인하는 메서드
     *
     * refreshToken이 유효하지 않으면 재발급을 할 수 없으므로 예외 발생
     */
    private void validateRefreshToken(String refreshToken) {
        if (!this.jwtUtil.validateToken(refreshToken)) {
            log.error("JWT Refresh Token is invalid during the '/reissue' process.");
            throw new JwtAuthenticationException(ErrorStatus._REISSUE_ERROR);
        }
    }

    /**
     * refreshToken이 redis에 저장된 refreshToken과 일치하는지 확인하는 메서드
     *
     * 두 refreshToken이 일치하지 않으면 잘못된 refreshToken이므로 예외 발생
     */
    private void isRefreshTokenMatch(String refreshToken) {
        JwtClaimsDto jwtClaimsDto = jwtUtil.getJwtClaimsFromRefreshToken(refreshToken);

        if (stringRedisUtil.getData(stringRedisUtil.getRedisKeyForJwt(jwtClaimsDto)) == null ||
                !refreshToken.equals(stringRedisUtil.getData(stringRedisUtil.getRedisKeyForJwt(jwtClaimsDto)))) {

            log.error("WT Refresh Token is either missing in Redis or does not match the token in Redis.");
            throw new JwtAuthenticationException(ErrorStatus._REISSUE_ERROR);
        }
    }

    /**
     * refresh token 재발급 하는 메서드
     *
     * 1. 새로운 Refresh Token 발급
     * 2. 해당 Key 에 해당하는 Redis Value 업데이트
     **/
    private String reIssueRefreshToken(JwtClaimsDto jwtClaimsDto) {
        // 기존 refresh token 삭제
        stringRedisUtil.deleteData(stringRedisUtil.getRedisKeyForJwt(jwtClaimsDto));
        String reIssuedRefreshToken = jwtUtil.createRefreshToken(jwtClaimsDto);
        // refresh token 저장
        stringRedisUtil.setDataExpire(stringRedisUtil.getRedisKeyForJwt(jwtClaimsDto), reIssuedRefreshToken, jwtProperties.getRefreshExpiration());
        return reIssuedRefreshToken;
    }

    /**
     * 재발급한 refresh & access token을 응답으로 보내는 메서드
     *
     * 1. 상태 코드 설정
     * 2. 응답 헤더에 설정 (jwtProperties 에서 정보 가져옴)
     **/
    private void makeAndSendAccessTokenAndRefreshToken(HttpServletResponse response,
                                                       String accessToken,
                                                       String refreshToken) throws IOException {
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(this.jwtProperties.getAccessExpiration() / 1000);
        // refresh token, access token 을 응답 본문에 넣어 응답
        JwtTokenResponseDto jwtTokenResponseDto = JwtTokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredTime(expireTime)
                .build();
        makeResultResponse(response, jwtTokenResponseDto);
    }

    /**
     * 재발급한 토큰이 담긴 응답을 client에게 보내는 메서드
     */
    private void makeResultResponse(HttpServletResponse response,
                                    JwtTokenResponseDto jwtTokenResponseDto) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (OutputStream os = response.getOutputStream()) {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            ResponseDto<JwtTokenResponseDto> responseDto = ResponseDto.onSuccess(jwtTokenResponseDto);
            objectMapper.writeValue(os, responseDto);
            os.flush();
        }
    }

    /**
     * accessToken이 유효한지 확인하고 다음 필터로 jwtClaims를 보내는 메서드
     *
     * accessToken이 유효하지 않으면 인증 예외 발생
     */
    private void checkAccessToken(HttpServletRequest request) {

        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> {
                    log.error("Access Token is missing in the Authorization header.");
                    return new JwtAuthenticationException(ErrorStatus._UNAUTHORIZED);
                });

        JwtClaimsDto jwtClaimsDto = jwtUtil.getJwtClaimsFromAccessToken(accessToken);

        request.setAttribute("jwtClaims", jwtClaimsDto);
    }

}
