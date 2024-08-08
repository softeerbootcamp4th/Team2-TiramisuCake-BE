package com.softeer.backend.fo_domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softeer.backend.global.common.constant.ValidationConstant;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
