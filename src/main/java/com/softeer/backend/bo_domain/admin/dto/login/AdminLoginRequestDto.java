package com.softeer.backend.bo_domain.admin.dto.login;

import com.softeer.backend.global.common.constant.ValidationConstant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class AdminLoginRequestDto {

    @NotNull
    private String account;

    @NotNull
    private String password;
}
