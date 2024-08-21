package com.softeer.backend.bo_domain.admin.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.softeer.backend.bo_domain.admin.dto.event.DrawEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.dto.event.EventPageResponseDto;
import com.softeer.backend.bo_domain.admin.dto.event.FcfsEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.dto.main.AdminMainPageResponseDto;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import com.softeer.backend.fo_domain.fcfs.service.FcfsSettingManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * 어드민 페이지의 이벤트 관리 페이지 요청을 처리하는 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EventPageService {

    private final FcfsSettingRepository fcfsSettingRepository;
    private final DrawSettingRepository drawSettingRepository;

    /**
     * 이벤트 관리 페이지 정보를 반환하는 메서드
     */
    @Transactional(readOnly = true)
    public EventPageResponseDto getEventPage() {

        return EventPageResponseDto.of(fcfsSettingRepository.findAll(), drawSettingRepository.findAll().get(0));
    }

    /**
     * 선착순 이벤트 시간을 수정하는 메서드
     * <p>
     * 1. DB에 있는 선착순 이벤트 시간 속성을 수정한다.
     * 2. DB에 있는 추첨 이벤트 시간 속성을 수정한다. (추첨 이벤트 날짜는 선착순 이벤트 날짜에 종속적)
     */
    public void updateFcfsEventTime(FcfsEventTimeRequestDto fcfsEventTimeRequestDto) {

        List<FcfsSetting> fcfsSettingList = fcfsSettingRepository.findAll(Sort.by(Sort.Order.asc("id")));

        LocalDate startDate = fcfsEventTimeRequestDto.getStartDate();
        LocalDate endDate = fcfsEventTimeRequestDto.getEndDate();
        LocalTime startTime = fcfsEventTimeRequestDto.getStartTime();

        // 선착순 1, 2라운드 시간 및 날짜 속성 수정
        updateFcfsSetting(fcfsSettingList.get(0), startDate, startTime);
        updateFcfsSetting(fcfsSettingList.get(1), endDate, startTime);

        LocalDate nextWeekStartDate = startDate.plusWeeks(1);
        LocalDate nextWeekEndDate = endDate.plusWeeks(1);

        // 선착순 3, 4라운드 시간 및 날짜 속성 수정
        updateFcfsSetting(fcfsSettingList.get(2), nextWeekStartDate, startTime);
        updateFcfsSetting(fcfsSettingList.get(3), nextWeekEndDate, startTime);

        // 추첨 이벤트 시간 및 날짜 속성 수정
        DrawSetting drawSetting = drawSettingRepository.findAll().get(0);
        updateDrawSetting(drawSetting, startDate, endDate);

    }

    /**
     * 선착순 속성 entity의 날짜 및 시간 속성을 수정하는 메서드
     */
    private void updateFcfsSetting(FcfsSetting fcfsSetting, LocalDate date, LocalTime time) {

        LocalDateTime newStartTime = LocalDateTime.of(date, time);
        LocalDateTime newEndTime = newStartTime.plusHours(2);

        fcfsSetting.setStartTime(newStartTime);
        fcfsSetting.setEndTime(newEndTime);

    }

    /**
     * 추첨 속성 entity의 날짜 속성을 수정하는 메서드
     */
    private void updateDrawSetting(DrawSetting drawSetting, LocalDate startDate, LocalDate endDate) {
        LocalDate startDateOfDraw = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate endDateOfPreviousWeek = endDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate endDateOfDraw = endDateOfPreviousWeek.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

        drawSetting.setStartDate(startDateOfDraw);
        drawSetting.setEndDate(endDateOfDraw);

    }

    /**
     * 추첨 이벤트의 시간 속성을 수정하는 메서드
     */
    public void updateDrawEventTime(DrawEventTimeRequestDto drawEventTimeRequestDto) {
        DrawSetting drawSetting = drawSettingRepository.findAll().get(0);

        drawSetting.setStartTime(drawEventTimeRequestDto.getStartTime());
        drawSetting.setEndTime(drawEventTimeRequestDto.getEndTime());

    }


}
