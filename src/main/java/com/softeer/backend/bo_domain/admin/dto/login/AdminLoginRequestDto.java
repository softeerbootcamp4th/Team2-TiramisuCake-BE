package com.softeer.backend.bo_domain.admin.dto.login;

import com.softeer.backend.global.common.constant.ValidationConstant;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class AdminLoginRequestDto {

    @Pattern(regexp = ValidationConstant.ADMIN_ACCOUNT_REGEX,
            message = ValidationConstant.ADMIN_ACCOUNT_MSG)
    private String account;

    @Pattern(regexp = ValidationConstant.ADMIN_PASSWORD_REGEX,
            message = ValidationConstant.ADMIN_PASSWORD_MSG)
    private String password;
}
