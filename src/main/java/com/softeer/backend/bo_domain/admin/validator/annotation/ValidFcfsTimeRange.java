package com.softeer.backend.bo_domain.admin.validator.annotation;

import com.softeer.backend.bo_domain.admin.validator.FcfsTimeRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FcfsTimeRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFcfsTimeRange {
    String message() default "시작 시간값은 09:00:00 ~ 18:00:00 범위에 속해야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}