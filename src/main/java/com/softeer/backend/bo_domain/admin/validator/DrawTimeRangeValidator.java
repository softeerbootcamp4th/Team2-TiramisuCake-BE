package com.softeer.backend.bo_domain.admin.validator;

import com.softeer.backend.bo_domain.admin.dto.event.DrawEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.validator.annotation.ValidDrawTimeRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;

/**
 * 추첨 이벤트 시간값 수정 요청 시, 시간값을 검사하는 애노테이션
 */
@Slf4j
public class DrawTimeRangeValidator implements ConstraintValidator<ValidDrawTimeRange, DrawEventTimeRequestDto> {

    @Override
    public void initialize(ValidDrawTimeRange constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(DrawEventTimeRequestDto value, ConstraintValidatorContext context) {
        if (value.getStartTime() == null || value.getEndTime() == null) {
            return true;
        }

        LocalTime startTime = value.getStartTime();
        LocalTime endTime = value.getEndTime();

        // 시작 시간 검증: 09:00:00 이후
        if (startTime.isBefore(LocalTime.of(9, 0))) {
            log.error("Start time should be before 09:00:00 at DrawEventTimeRequestDto");
            return false;
        }

        // 종료 시간 검증: 23:59:59 이전
        if (endTime.isAfter(LocalTime.of(23, 59, 59))) {
            log.error("End time should be after 23:59:59 at DrawEventTimeRequestDto");
            return false;
        }

        // 시작 시간이 종료 시간보다 이전인지 확인
        return !startTime.isAfter(endTime);
    }
}
