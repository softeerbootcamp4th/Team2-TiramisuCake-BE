package com.softeer.backend.bo_domain.admin.service;

import com.softeer.backend.bo_domain.admin.dto.indicator.EventIndicatorResponseDto;
import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import com.softeer.backend.bo_domain.eventparticipation.repository.EventParticipationRepository;
import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import com.softeer.backend.fo_domain.draw.repository.DrawSettingRepository;
import com.softeer.backend.fo_domain.draw.service.DrawSettingManager;
import kotlinx.serialization.Required;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 이벤트 지표 페이지 요청을 처리하는 클래스
 */
@Service
@RequiredArgsConstructor
public class IndicatorPageService {

    private final EventParticipationRepository eventParticipationRepository;
    private final DrawSettingRepository drawSettingRepository;

    /**
     * 이벤트 지표 데이터를 반환하는 메서드
     */
    public EventIndicatorResponseDto getEventIndicator() {

        DrawSetting drawSetting = drawSettingRepository.findAll().get(0);

        List<EventParticipation> eventParticipationList = eventParticipationRepository.findAllByEventDateBetween(
                drawSetting.getStartDate(), drawSetting.getEndDate()
        );

        return EventIndicatorResponseDto.of(eventParticipationList, drawSetting);
    }

}
