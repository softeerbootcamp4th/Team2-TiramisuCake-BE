package com.softeer.backend.fo_domain.user.dto;

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
public class LoginRequest {

    private String name;

    @Pattern(regexp = ValidationConstant.PHONE_NUMBER_REGEX,
            message = ValidationConstant.PHONE_NUMBER_MSG)
    private String phoneNumber;

    private boolean isCodeVerified;

    private boolean privacyConsent;

    private boolean marketingConsent;
}
