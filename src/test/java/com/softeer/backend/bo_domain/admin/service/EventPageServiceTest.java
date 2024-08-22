package com.softeer.backend.bo_domain.admin.service;

import com.softeer.backend.bo_domain.admin.dto.event.DrawEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.dto.event.EventPageResponseDto;
import com.softeer.backend.bo_domain.admin.dto.event.FcfsEventTimeRequestDto;
import com.softeer.backend.bo_domain.admin.service.EventPageService;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.fo_domain.fcfs.repository.FcfsSettingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Sort;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventPageServiceTest {
    @InjectMocks
    private EventPageService eventPageService;

    @Mock
    private FcfsSettingRepository fcfsSettingRepository;

    @Mock
    private DrawSettingRepository drawSettingRepository;

    @Test
    @DisplayName("선착순 이벤트 시간을 수정한다.")
    void updateFcfsEventTime() {
        // Given
        FcfsSetting fcfsSetting1 = new FcfsSetting();
        FcfsSetting fcfsSetting2 = new FcfsSetting();
        FcfsSetting fcfsSetting3 = new FcfsSetting();
        FcfsSetting fcfsSetting4 = new FcfsSetting();
        List<FcfsSetting> fcfsSettings = Arrays.asList(fcfsSetting1, fcfsSetting2, fcfsSetting3, fcfsSetting4);
        when(fcfsSettingRepository.findAll(Sort.by(Sort.Order.asc("id")))).thenReturn(fcfsSettings);

        DrawSetting drawSetting = new DrawSetting();
        when(drawSettingRepository.findAll()).thenReturn(List.of(drawSetting));

        LocalDate startDate = LocalDate.of(2024, 8, 1);
        LocalDate endDate = LocalDate.of(2024, 8, 2);
        LocalTime startTime = LocalTime.of(10, 0);

        FcfsEventTimeRequestDto requestDto = new FcfsEventTimeRequestDto(startDate, endDate, startTime);

        // When
        eventPageService.updateFcfsEventTime(requestDto);

        // Then
        assertThat(fcfsSetting1.getStartTime()).isEqualTo(LocalDateTime.of(startDate, startTime));
        assertThat(fcfsSetting1.getEndTime()).isEqualTo(LocalDateTime.of(startDate, startTime.plusHours(2)));

        assertThat(fcfsSetting2.getStartTime()).isEqualTo(LocalDateTime.of(endDate, startTime));
        assertThat(fcfsSetting2.getEndTime()).isEqualTo(LocalDateTime.of(endDate, startTime.plusHours(2)));

        assertThat(fcfsSetting3.getStartTime()).isEqualTo(LocalDateTime.of(startDate.plusWeeks(1), startTime));
        assertThat(fcfsSetting3.getEndTime()).isEqualTo(LocalDateTime.of(startDate.plusWeeks(1), startTime.plusHours(2)));

        assertThat(fcfsSetting4.getStartTime()).isEqualTo(LocalDateTime.of(endDate.plusWeeks(1), startTime));
        assertThat(fcfsSetting4.getEndTime()).isEqualTo(LocalDateTime.of(endDate.plusWeeks(1), startTime.plusHours(2)));

        verify(drawSettingRepository).findAll();
        assertThat(drawSetting.getStartDate()).isEqualTo(startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        assertThat(drawSetting.getEndDate()).isEqualTo(endDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
    }

    @Test
    @DisplayName("추첨 이벤트 시간을 수정한다.")
    void updateDrawEventTime() {
        // Given
        DrawSetting drawSetting = new DrawSetting();
        when(drawSettingRepository.findAll()).thenReturn(List.of(drawSetting));

        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        DrawEventTimeRequestDto requestDto = new DrawEventTimeRequestDto(startTime, endTime);

        // When
        eventPageService.updateDrawEventTime(requestDto);

        // Then
        assertThat(drawSetting.getStartTime()).isEqualTo(startTime);
        assertThat(drawSetting.getEndTime()).isEqualTo(endTime);
    }
}
