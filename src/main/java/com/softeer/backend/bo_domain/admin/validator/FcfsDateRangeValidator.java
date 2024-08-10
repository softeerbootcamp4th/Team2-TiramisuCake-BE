package com.softeer.backend.bo_domain.admin.validator;

import com.softeer.backend.bo_domain.admin.dto.event.FcfsEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.validator.annotation.ValidFcfsDateRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class FcfsDateRangeValidator implements ConstraintValidator<ValidFcfsDateRange, FcfsEventTimeRequestDto> {

    @Override
    public void initialize(ValidFcfsDateRange constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(FcfsEventTimeRequestDto value, ConstraintValidatorContext context) {
        if (value.getStartDate() == null || value.getEndDate() == null) {
            return true;
        }

        LocalDate startDate = value.getStartDate();
        LocalDate endDate = value.getEndDate();

        LocalDate startDateWeekStart = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endDateWeekStart = endDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        boolean isSameWeek = startDateWeekStart.equals(endDateWeekStart);

        // 시작 날짜가 종료 날짜보다 이전인지 확인
        boolean isStartBeforeEnd = !startDate.isAfter(endDate);

        // 두 검증 조건을 모두 만족하는지 확인
        return isSameWeek && isStartBeforeEnd;
    }
}