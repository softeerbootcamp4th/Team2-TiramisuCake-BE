package com.softeer.backend.bo_domain.admin.service;

import com.softeer.backend.bo_domain.admin.dto.indicator.EventIndicatorResponseDto;
import com.softeer.backend.bo_domain.admin.service.IndicatorPageService;
import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import com.softeer.backend.bo_domain.eventparticipation.repository.EventParticipationRepository;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicatorPageServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private DrawSettingRepository drawSettingRepository;

    @InjectMocks
    private IndicatorPageService indicatorPageService;

    private DrawSetting drawSetting;
    private List<EventParticipation> eventParticipationList;

    @BeforeEach
    void setUp() {

        // Mock data
        drawSetting = DrawSetting.builder()
                .startDate(LocalDate.of(2024, 8, 1))
                .endDate(LocalDate.of(2024, 8, 31))
                .build();

        eventParticipationList = List.of(
                EventParticipation.builder()
                        .eventDate(LocalDate.of(2024, 8, 1))
                        .visitorCount(100)
                        .fcfsParticipantCount(50)
                        .drawParticipantCount(30)
                        .build(),
                EventParticipation.builder()
                        .eventDate(LocalDate.of(2024, 8, 2))
                        .visitorCount(150)
                        .fcfsParticipantCount(75)
                        .drawParticipantCount(45)
                        .build()
        );
    }

    @Test
    @DisplayName("이벤트 지표 데이터를 정상적으로 반환한다.")
    void getEventIndicatorSuccess() {
        // Given
        when(drawSettingRepository.findAll()).thenReturn(List.of(drawSetting));
        when(eventParticipationRepository.findAllByEventDateBetween(drawSetting.getStartDate(), drawSetting.getEndDate()))
                .thenReturn(eventParticipationList);

        // When
        EventIndicatorResponseDto response = indicatorPageService.getEventIndicator();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStartDate()).isEqualTo(drawSetting.getStartDate());
        assertThat(response.getEndDate()).isEqualTo(drawSetting.getEndDate());
        assertThat(response.getTotalVisitorCount()).isEqualTo(250); // 100 + 150
        assertThat(response.getTotalFcfsParticipantCount()).isEqualTo(125); // 50 + 75
        assertThat(response.getTotalDrawParticipantCount()).isEqualTo(75); // 30 + 45
        assertThat(response.getFcfsParticipantRate()).isEqualTo(125.0 / 250.0); // 125 / 250
        assertThat(response.getDrawParticipantRate()).isEqualTo(75.0 / 250.0); // 75 / 250
        assertThat(response.getVisitorNumList()).hasSize(2);
        assertThat(response.getVisitorNumList().get(0).getVisitDate()).isEqualTo(LocalDate.of(2024, 8, 1));
        assertThat(response.getVisitorNumList().get(0).getVisitorNum()).isEqualTo(100);
        assertThat(response.getVisitorNumList().get(1).getVisitDate()).isEqualTo(LocalDate.of(2024, 8, 2));
        assertThat(response.getVisitorNumList().get(1).getVisitorNum()).isEqualTo(150);
    }
}
