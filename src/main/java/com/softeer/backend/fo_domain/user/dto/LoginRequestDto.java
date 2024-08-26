package com.softeer.backend.fo_domain.user.dto;


import com.softeer.backend.global.common.constant.ValidationConstant;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청 DTO 클래스
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginRequestDto {

    private String name;

    @Pattern(regexp = ValidationConstant.PHONE_NUMBER_REGEX,
            message = ValidationConstant.PHONE_NUMBER_MSG)
    private String phoneNumber;

    private Boolean hasCodeVerified;

    private Boolean privacyConsent;

    private Boolean marketingConsent;
}
