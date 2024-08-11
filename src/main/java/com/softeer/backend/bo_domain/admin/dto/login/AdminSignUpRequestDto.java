package com.softeer.backend.bo_domain.admin.dto.login;

import jakarta.validation.constraints.NotNull;
import lombok.*;

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
