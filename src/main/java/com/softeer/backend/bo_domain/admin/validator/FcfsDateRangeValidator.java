package com.softeer.backend.bo_domain.admin.validator;

import com.softeer.backend.bo_domain.admin.dto.event.FcfsEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.validator.annotation.ValidFcfsDateRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * 선착순 이벤트 날짜 수정 시, 해당 날짜를 검사하는 애노테이션
 */
@Slf4j
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

        LocalDate startDateWeekStart = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDateWeekStart = endDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 선착순 날짜가 같은 주에 있는지를 확인(월~일)
        boolean isSameWeek = startDateWeekStart.equals(endDateWeekStart);
        if(!isSameWeek)
            log.error("Start date and end date should be same week at FcfsEventTimeRequestDto");

        // 시작 날짜가 종료 날짜보다 이전인지 확인
        boolean isStartBeforeEnd = !startDate.isAfter(endDate);
        if(!isStartBeforeEnd)
            log.error("Start date should be before end date at FcfsEventTimeRequestDto");

        // 두 검증 조건을 모두 만족하는지 확인
        return isSameWeek && isStartBeforeEnd;
    }
}