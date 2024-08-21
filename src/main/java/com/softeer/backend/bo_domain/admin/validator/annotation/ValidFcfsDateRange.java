package com.softeer.backend.bo_domain.admin.validator.annotation;

import com.softeer.backend.bo_domain.admin.validator.FcfsDateRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 선착순 이벤트 날짜 수정 시, 해당 날짜를 검사하는 애노테이션
 */
@Constraint(validatedBy = FcfsDateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFcfsDateRange {
    String message() default "선착순 날짜는 같은 주에 있어야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}