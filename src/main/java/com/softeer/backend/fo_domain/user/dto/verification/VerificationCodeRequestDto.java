package com.softeer.backend.fo_domain.user.dto.verification;

import com.softeer.backend.global.common.constant.ValidationConstant;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * 전화번호 인증 코드 요청 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class VerificationCodeRequestDto {

    @Pattern(regexp = ValidationConstant.PHONE_NUMBER_REGEX,
            message = ValidationConstant.PHONE_NUMBER_MSG)
    private String phoneNumber;
}
