package com.softeer.backend.bo_domain.admin.dto.login;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 어드민 회원가입 요청 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class AdminSignUpRequestDto {

    @NotNull
    private String account;

    @NotNull
    private String password;

}
