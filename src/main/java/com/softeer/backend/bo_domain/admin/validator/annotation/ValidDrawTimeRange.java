package com.softeer.backend.bo_domain.admin.validator.annotation;

import com.softeer.backend.bo_domain.admin.validator.DrawTimeRangeValidator;
import com.softeer.backend.bo_domain.admin.validator.FcfsDateRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 추첨 이벤트 시간값 수정 요청 시, 시간값을 검사하는 애노테이션
 */
@Constraint(validatedBy = DrawTimeRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDrawTimeRange {
    String message() default "시작시간은 09:00 이후, 종료 시간은 11:59:59 이전이어야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
