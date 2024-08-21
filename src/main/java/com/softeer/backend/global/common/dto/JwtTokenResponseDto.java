package com.softeer.backend.global.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JWT 응답 DTO 클래스
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class JwtTokenResponseDto {

    private String accessToken;

    private String refreshToken;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredTime;

}
