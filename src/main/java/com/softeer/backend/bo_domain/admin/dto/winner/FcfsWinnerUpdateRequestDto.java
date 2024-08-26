package com.softeer.backend.bo_domain.admin.dto.winner;

import com.softeer.backend.global.common.constant.ValidationConstant;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 선착순 당첨자 수 수정 요청 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class FcfsWinnerUpdateRequestDto {

    @NotNull
    @Min(value = 1, message = ValidationConstant.MIN_VALUE_MSG)
    @Max(value = 50, message = ValidationConstant.MAX_VALUE_MSG)
    private Integer fcfsWinnerNum;

}
