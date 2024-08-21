package com.softeer.backend.bo_domain.admin.validator;

import com.softeer.backend.bo_domain.admin.dto.event.FcfsEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.validator.annotation.ValidFcfsTimeRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;

/**
 * 선착순 이벤트 시간값 수정 시, 해당 시간값을 검사하는 애노테이션
 */
@Slf4j
public class FcfsTimeRangeValidator implements ConstraintValidator<ValidFcfsTimeRange, FcfsEventTimeRequestDto> {

    @Override
    public void initialize(ValidFcfsTimeRange constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(FcfsEventTimeRequestDto value, ConstraintValidatorContext context) {
        if (value.getStartTime() == null) {
            return true;
        }

        LocalTime startTime = value.getStartTime();

        // 시작 시간이 오전 9시 이후인지 검증
        boolean isStartTimeValid = !startTime.isBefore(LocalTime.of(9, 0));
        if(!isStartTimeValid)
            log.error("Start time should be equal and after 9:00:00 at FcfsEventTimeRequestDto");

        // 시작 시간이 오후 6시 이전인지 검증
        boolean isEndTimeValid = !startTime.isAfter(LocalTime.of(18, 0));
        if(!isEndTimeValid)
            log.error("End time should be equal and before 18:00:00 at FcfsEventTimeRequestDto");

        // 모든 검증 조건이 만족되는지 확인
        return isStartTimeValid && isEndTimeValid;
    }
}